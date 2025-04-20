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

import io.github.agentsoz.jill.lang.Agent;
import io.github.agentsoz.jill.lang.Goal;
import io.github.agentsoz.jill.lang.Plan;
import io.github.agentsoz.jill.lang.PlanStep;

import java.util.Map;


public class PlanGoHomeThenLeave extends Plan {

	EgaleoAgent agent = null;
	public boolean goingHomeBeforeLeaving = false;

	public PlanGoHomeThenLeave(Agent agent, Goal goal, String name) {
		super(agent, goal, name);
		this.agent = (EgaleoAgent)agent;
		body = steps;
	}

	public boolean context() {
		boolean applicable = false;
		if (agent.isFinalResponseThresholdBreached() && agent.getDependentInfo() == null) {
			applicable = true;
		}
		agent.memorise(EgaleoAgent.MemoryEventType.DECIDED.name(), EgaleoAgent.MemoryEventValue.IS_PLAN_APPLICABLE.name()
				+ ":" + getGoal() + "|" + this.getClass().getSimpleName() + "=" + applicable);
		return applicable;
	}

	PlanStep[] steps = {
			() -> {
				agent.memorise(EgaleoAgent.MemoryEventType.ACTIONED.name(), getGoal() + "|" + this.getClass().getSimpleName());
				if (agent.getWillGoHomeBeforeLeaving()) {
					goingHomeBeforeLeaving =true;
					subgoal(new GoalGoHome("GoalGoHome"));
					// Now wait till the next step for this goal to finish
				}
			},
			() -> {
				subgoal(new GoalGotoEvacPlace("GoalGotoEvacPlace"));
				// Now wait till the next step for this goal to finish
			},
			() -> {
				// Arrived at evac location
				agent.memorise(EgaleoAgent.MemoryEventType.BELIEVED.name(), EgaleoAgent.MemoryEventValue.SAFE.name());
			},
	};

	@Override
	public void setPlanVariables(Map<String, Object> vars) {
	}
}
