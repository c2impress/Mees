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

import io.github.agentsoz.jill.lang.Agent;
import io.github.agentsoz.jill.lang.Goal;
import io.github.agentsoz.jill.lang.Plan;
import io.github.agentsoz.jill.lang.PlanStep;

import java.util.Map;


public class PlanLeaveNow extends Plan {

	MallorcaAgent agent = null;

	public PlanLeaveNow(Agent agent, Goal goal, String name) {
		super(agent, goal, name);
		this.agent = (MallorcaAgent)agent;
		body = steps;
	}

	public boolean context() {
		boolean applicable = false;
		if (agent.isFinalResponseThresholdBreached() && agent.getDependentInfo() != null) {
			applicable = true;
		}
		agent.memorise(MallorcaAgent.MemoryEventType.DECIDED.name(), MallorcaAgent.MemoryEventValue.IS_PLAN_APPLICABLE.name()
				+ ":" + getGoal() + "|" + this.getClass().getSimpleName() + "=" + true);
		return applicable;
	}

	PlanStep[] steps = {
			() -> {
				agent.memorise(MallorcaAgent.MemoryEventType.ACTIONED.name(), getGoal() + "|" + this.getClass().getSimpleName());
				subgoal(new GoalGotoEvacPlace("GoalGotoEvacPlace"));
				// Now wait till the next step for this goal to finish
			},
			() -> {
				// Arrived at the evac location
				agent.memorise(MallorcaAgent.MemoryEventType.BELIEVED.name(), MallorcaAgent.MemoryEventValue.SAFE.name());
			},
	};

	@Override
	public void setPlanVariables(Map<String, Object> vars) {
	}
}
