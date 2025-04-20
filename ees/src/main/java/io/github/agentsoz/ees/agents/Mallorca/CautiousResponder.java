package io.github.agentsoz.ees.agents.Mallorca;

import io.github.agentsoz.ees.agents.archetype.ArchetypeAgent;
import io.github.agentsoz.jill.lang.AgentInfo;

@AgentInfo(hasGoals={
        "io.github.agentsoz.ees.agents.archetype.GoalFullResponse",
        "io.github.agentsoz.ees.agents.archetype.GoalInitialResponse",
        "io.github.agentsoz.ees.agents.archetype.GoalFinalResponse",
        "io.github.agentsoz.ees.agents.archetype.GoalGoto",
        "io.github.agentsoz.abmjill.genact.EnvironmentAction",


})
public class CautiousResponder extends ArchetypeAgent {

    private Prefix prefix = new CautiousResponder.Prefix();

    public CautiousResponder(String id) {
        super(id);
    }

    class Prefix {
        public String toString() {
            return String.format("%05.0f|%s|%s|%s|", getTime(), getTimeString(), PracticalResponder.class.getSimpleName(), getId());
        }
    }

    @Override
    public String logPrefix() {
        return prefix.toString();
    }
}
