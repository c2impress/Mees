package io.github.agentsoz.ees.agents.Egaleo;

/*-
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
import io.github.agentsoz.ees.Constants;
import io.github.agentsoz.jill.lang.Agent;
import io.github.agentsoz.jill.lang.Goal;
import io.github.agentsoz.jill.lang.Plan;
import io.github.agentsoz.jill.lang.PlanStep;
import io.github.agentsoz.util.Location;

import java.util.Map;


public class PlanGotoDependentsW1 extends Plan {

	EgaleoAgentW1 agent = null;
	private Location destination = null;
	boolean startedDriving = false;

	public PlanGotoDependentsW1(Agent agent, Goal goal, String name) {
		super(agent, goal, name);
		this.agent = (EgaleoAgentW1)agent;
		body = steps;
	}

	public boolean context() {
		boolean applicable = true;
		agent.memorise(EgaleoAgentW1.MemoryEventType.DECIDED.name(), EgaleoAgentW1.MemoryEventValue.IS_PLAN_APPLICABLE.name()
				+ ":" + getGoal() + "|" + this.getClass().getSimpleName() + "=" + applicable);
		return applicable;
	}

	PlanStep[] steps = {
			() -> {
				agent.memorise(EgaleoAgentW1.MemoryEventType.ACTIONED.name(), getGoal() + "|" + this.getClass().getSimpleName());
				// start driving to destination, or do nothing if already there
				destination = agent.getDependentInfo().getLocation();
				startedDriving = agent.startWalkingTo1(destination, Constants.EvacRoutingMode.sOneFree);
			},
			() -> {
				if (startedDriving) {
					// Step subsequent to post must suspend agent when waiting for external stimuli
					// Will be reset by updateAction()
					agent.suspend(true);
					// Do not add any checks here since the above call is non-blocking
					// Suspend will happen once this step is finished
				}
			},
			() -> {
				// Try a second time
				startedDriving = false;
				if (agent.getLastDriveActionStatus() != ActionContent.State.DROPPED && agent.getTravelDistanceTo(destination)  > 0.0) {
					startedDriving = agent.startWalkingTo1(destination, Constants.EvacRoutingMode.sOneGlobal);
				}
			},
			() -> {
				if (startedDriving) {
					// Step subsequent to post must suspend agent when waiting for external stimuli
					// Will be reset by updateAction()
					agent.suspend(true);
					// Do not add any checks here since the above call is non-blocking
					// Suspend will happen once this step is finished
				}
			},
			() -> {
				// Try a third time
				startedDriving = false;
				if (agent.getLastDriveActionStatus() != ActionContent.State.DROPPED && agent.getTravelDistanceTo(destination)  > 0.0) {
					startedDriving = agent.startWalkingTo1(destination, Constants.EvacRoutingMode.sOneGlobal);
				}
			},
			() -> {
				if (startedDriving) {
					// Step subsequent to post must suspend agent when waiting for external stimuli
					// Will be reset by updateAction()
					agent.suspend(true);
					// Do not add any checks here since the above call is non-blocking
					// Suspend will happen once this step is finished
				}
			},
			() -> {
				// Hopefully we are there, else we will give up now after three tries
				agent.getTravelDistanceTo(destination);
				agent.memorise(EgaleoAgentW1.MemoryEventType.BELIEVED.name(),
						EgaleoAgentW1.MemoryEventValue.DISTANCE_TO_LOCATION.name()
								+ ":"+ destination
								+ ":" + String.format("%.0f", agent.getTravelDistanceTo(destination)) + "m");
			},
	};

	@Override
	public void setPlanVariables(Map<String, Object> vars) {
	}
}
