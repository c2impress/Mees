package io.github.agentsoz.ees.agents.Mallorca;

/*-
 * #%L
 * Emergency Evacuation Simulator
 * %%
 * Copyright (C) 2014 - 2024 by its authors. See AUTHORS file.
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

import io.github.agentsoz.ees.Constants;
import io.github.agentsoz.jill.lang.Agent;
import io.github.agentsoz.jill.lang.Goal;
import io.github.agentsoz.jill.lang.Plan;
import io.github.agentsoz.jill.lang.PlanStep;

import java.util.Map;


public class PlanReplanToDestination extends Plan {

	MallorcaAgent agent = null;
	boolean isReplanning = false;

	public PlanReplanToDestination(Agent agent, Goal goal, String name) {
		super(agent, goal, name);
		body = steps;
		this.agent = (MallorcaAgent)agent;

	}

	public boolean context() {
		((MallorcaAgent)getAgent()).memorise(MallorcaAgent.MemoryEventType.DECIDED.name(), MallorcaAgent.MemoryEventValue.IS_PLAN_APPLICABLE.name()
				+ ":" + getGoal() + "|" + this.getClass().getSimpleName() + "=" + true);
		return true;
	}

	PlanStep[] steps = {
			() -> {
				((MallorcaAgent)getAgent()).memorise(MallorcaAgent.MemoryEventType.DECIDED.name(), MallorcaAgent.MemoryEventValue.DONE_FOR_NOW.name());
				agent.memorise(MallorcaAgent.MemoryEventType.ACTIONED.name(), getGoal() + "|" + this.getClass().getSimpleName());
				isReplanning = agent.replanCurrentDriveTo(Constants.EvacRoutingMode.carGlobalInformation);
			},
			() -> {
				if (isReplanning) {
					// Step subsequent to post must suspend agent when waiting for external stimuli
					// Will be reset by updateAction()
					agent.suspend(true);
					// Do not add any checks here since the above call is non-blocking
					// Suspend will happen once this step is finished
				}
			},
			() -> {
				agent.memorise(MallorcaAgent.MemoryEventType.BELIEVED.name(),
						MallorcaAgent.MemoryEventValue.STATE_CHANGED.name()
								+ ": replanned route to destination");
			},
	};

	@Override
	public void setPlanVariables(Map<String, Object> vars) {
	}
}
