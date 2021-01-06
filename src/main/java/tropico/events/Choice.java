package tropico.events;

public class Choice {

    private final String label;
    private final Effects effects;
    private final Event next;


    public Choice(String label, Effects effects, Event next) {
        this.label = label;
        this.effects = effects;
        this.next = next;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "label='" + label + '\'' +
                ", effects=" + effects +
                '}';
    }

    public String getLabel() {
        return label;
    }


}
