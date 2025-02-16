package io.github.agentsoz.ees.matsim;

/*
 * #%L
 * Emergency Evacuation Simulator
 * %%
 * Copyright (C) 2014 - 2025 by its authors. See AUTHORS file.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import io.github.agentsoz.bdiabm.data.ActionContent;
import io.github.agentsoz.bdiabm.data.PerceptContent;
import io.github.agentsoz.bdimatsim.EventsMonitorRegistry.MonitoredEventType;
import io.github.agentsoz.bdimatsim.MATSimModel;
import io.github.agentsoz.ees.Constants;
import io.github.agentsoz.nonmatsim.BDIActionHandler;
import io.github.agentsoz.nonmatsim.BDIPerceptHandler;
import io.github.agentsoz.nonmatsim.EventData;
import io.github.agentsoz.nonmatsim.PAAgent;
import io.github.agentsoz.util.Global;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.framework.PlanAgent;
import org.matsim.core.mobsim.qsim.agents.WithinDayAgentUtils;
import org.matsim.core.network.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EvacWalkto1ActionHandlerV2 implements BDIActionHandler {
	private static final Logger log = LoggerFactory.getLogger(EvacWalkto1ActionHandlerV2.class ) ;

	private final MATSimModel model;
	public EvacWalkto1ActionHandlerV2(MATSimModel model ) {
//		log.setLevel(Level.DEBUG);
		this.model = model;
	}
	@Override
	public ActionContent.State handle(String agentID, String actionID, Object[] args) {
		log.debug("------------------------------------------------------------------------------------------");

		// Ensure correct number of arguments
		if (args.length < 4) {
			log.error("[ERROR] Agent " + agentID + " WALKTO1 handler has " + args.length + " args (>=4 expected); ignoring event.");
			return ActionContent.State.RUNNING; // FIXME: seems incorrect to return RUNNING
		}

		// Validate argument types
		Gbl.assertIf(args.length >= 4);
		Gbl.assertIf(args[1] instanceof double[]);
		double[] coords = (double[]) args[1];
		Coord coord = new Coord(coords[0], coords[1]);
		Gbl.assertIf(args[3] instanceof Constants.EvacRoutingMode);

		log.debug("[DEBUG] Replanning step at time: " + model.getTime() +
				", Agent ID: " + agentID + ", Routing Mode: " + args[3]);

		// Retrieve the MobsimAgent
		System.out.println("[DEBUG] Attempting to retrieve MobsimAgent with agentID: " + agentID);
		MobsimAgent mobsimAgent = model.getMobsimAgentFromIdString(agentID);

		if (mobsimAgent == null) {
			log.error("[ERROR] MobsimAgent " + agentID + " no longer exists in QSim; ignoring action: " + actionID);
			return ActionContent.State.FAILED;
		}

		System.out.println("[DEBUG] Retrieved MobsimAgent: " + mobsimAgent);

		// Debug: Check what the current plan element is
		Object currentPlanElement = WithinDayAgentUtils.getCurrentPlanElement(mobsimAgent);
		System.out.println("[DEBUG] Agent " + agentID + " Current Plan Element Type: " +
				(currentPlanElement != null ? currentPlanElement.getClass().getSimpleName() : "NULL"));

		// Find the nearest link to the target coordinates
		final Link nearestLink = NetworkUtils.getNearestLink(model.getScenario().getNetwork(), coord);
		Gbl.assertNotNull(nearestLink);
		Id<Link> newLinkId = nearestLink.getId();
		System.out.println("[DEBUG] Agent " + agentID + " New Destination Link ID: " + newLinkId);

		// Reschedule activity if agent is at a real activity
		if (model.getReplanner().editPlans().isAtRealActivity(mobsimAgent)) {
			System.out.println("[DEBUG] Rescheduling current activity end time for Agent " + agentID);
			model.getReplanner().editPlans().rescheduleCurrentActivityEndtime(mobsimAgent, (double) args[2]);
		}

		// Determine routing mode
		String routingMode = null;
		switch (((Constants.EvacRoutingMode) args[3])) {
			case sOneFree:
				routingMode = Constants.EvacRoutingMode.sOneFree.name();
				break;
			case sOneGlobal:
				routingMode = Constants.EvacRoutingMode.sOneGlobal.name();
				break;
			default:
				log.error("[ERROR] Unknown routing mode for Agent " + agentID + ": " + args[3]);
				throw new RuntimeException("Unsupported routing mode");
		}
		System.out.println("[DEBUG] Agent " + agentID + " Selected Routing Mode: " + routingMode);

		// Print plan before flushing
		System.out.println("[DEBUG] Before flush:");
		printPlan("Before flush", mobsimAgent);

		// Flush plan beyond current state
		System.out.println("[DEBUG] Before flush: Vehicle ID = " + mobsimAgent.getId());
		model.getReplanner().editPlans().flushEverythingBeyondCurrent(mobsimAgent);
		System.out.println("[DEBUG] After flush: Vehicle ID = " + mobsimAgent.getId());


		// Print plan after flushing
		System.out.println("[DEBUG] After flush:");
		printPlan("After flush", mobsimAgent);

		// Determine if a replan activity should be added
		boolean addReplanActivity = (args.length >= 6 && args[5] instanceof Boolean) ? (Boolean) args[5] : false;
		int replanTime = (args.length >= 7 && args[6] instanceof Integer) ? (int) args[6] : 0;

		Activity rnewAct = null;
		if (addReplanActivity) {
			System.out.println("[DEBUG] Adding Replan Activity for Agent " + agentID);
			final Link link = model.getScenario().getNetwork().getLinks().get(mobsimAgent.getCurrentLinkId());
			rnewAct = model.getReplanner().editPlans().createFinalActivity("Replan", link.getId());
			rnewAct.setStartTime(model.getTime());
			rnewAct.setEndTime(model.getTime() + replanTime);
			model.getReplanner().editPlans().addActivityAtEnd(mobsimAgent, rnewAct, routingMode);
			printPlan("After adding replan activity", mobsimAgent);
		}

		// Create and add final evacuation activity
		String activity = (args.length >= 5 && args[4] instanceof String) ? (String) args[4] : "WalkTo1";
		System.out.println("[DEBUG] Adding new evacuation activity: " + activity + " for Agent " + agentID);
		Activity newAct = model.getReplanner().editPlans().createFinalActivity(activity, newLinkId);
		model.getReplanner().editPlans().addActivityAtEnd(mobsimAgent, newAct, routingMode);
		printPlan("After adding evac activity", mobsimAgent);

		// Insert empty trip if replan activity was added
		if (addReplanActivity) {
			System.out.println("[DEBUG] Inserting empty trip between replan and evac activities for Agent " + agentID);
			model.getReplanner().editTrips().insertEmptyTrip(
					WithinDayAgentUtils.getModifiablePlan(mobsimAgent),
					rnewAct, newAct, routingMode);
		}

		// Register agent in evacuation manager
		model.getAgentManager().getAgentsPerformingBdiWalkTo1().put(agentID, newLinkId.toString());

		log.debug("------------------------------------------------------------------------------------------");
		return ActionContent.State.RUNNING;
	}


	private void printPlan(String str ,MobsimAgent agent1) {
		Plan plan = WithinDayAgentUtils.getModifiablePlan(agent1) ;
		log.debug(str + plan ); ;
		for ( PlanElement pe : plan.getPlanElements() ) {
			log.debug("    " + pe ); ;
		}
	}
}
