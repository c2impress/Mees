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
import io.github.agentsoz.bdimatsim.MATSimModel;
import io.github.agentsoz.ees.Constants;
import io.github.agentsoz.nonmatsim.BDIActionHandler;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.qsim.agents.WithinDayAgentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReplanWalkTo1DefaultActionHandlerV2 implements BDIActionHandler {
	private static final Logger log = LoggerFactory.getLogger(ReplanWalkTo1DefaultActionHandlerV2.class ) ;

	private final MATSimModel model;
	public ReplanWalkTo1DefaultActionHandlerV2(MATSimModel model ) {
		this.model = model;
	}
	@Override

	public ActionContent.State handle(String agentID, String actionID, Object[] args) {
		System.out.println("[DEBUG] Handling ReplanWalkTo1 for Agent " + agentID + ", Action: " + actionID);

		// Retrieve agent
		MobsimAgent mobsimAgent = model.getMobsimAgentFromIdString(agentID);
		if (mobsimAgent == null) {
			System.out.println("[ERROR] MobsimAgent " + agentID + " not found; ignoring action.");
			return ActionContent.State.FAILED;
		}

		System.out.println("[DEBUG] Retrieved MobsimAgent: " + mobsimAgent);

		// Validate routing mode
		Gbl.assertIf(args.length >= 1);
		Gbl.assertIf(args[0] instanceof Constants.EvacRoutingMode);
		Constants.EvacRoutingMode routingMode = (Constants.EvacRoutingMode) args[0];

		// Retrieve the current plan element
		PlanElement currentPlanElement = WithinDayAgentUtils.getCurrentPlanElement(mobsimAgent);
		System.out.println("[DEBUG] Agent " + agentID + " Current Plan Element: " +
				(currentPlanElement != null ? currentPlanElement.getClass().getSimpleName() : "NULL"));

		// Debug vehicle ID before replanning
		if (mobsimAgent instanceof MobsimDriverAgent) {
			System.out.println("[DEBUG] Before replanning: Vehicle ID = " +
					((MobsimDriverAgent) mobsimAgent).getPlannedVehicleId());
		}

		// Replan only if the agent is currently in a trip
		if (currentPlanElement instanceof Leg) {
			System.out.println("[DEBUG] Agent " + agentID + " is in a LEG; initiating trip replanning.");
			model.getReplanner().editTrips().replanCurrentTrip(mobsimAgent, 0.0, routingMode.name());

			// Debug vehicle ID after replanning
			if (mobsimAgent instanceof MobsimDriverAgent) {
				System.out.println("[DEBUG] After replanning: Vehicle ID = " +
						((MobsimDriverAgent) mobsimAgent).getPlannedVehicleId());
			}
		} else {
			System.out.println("[WARNING] Agent " + agentID + " is not in a LEG; skipping trip replanning.");
		}

		return ActionContent.State.PASSED;
	}

}
