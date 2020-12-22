package tropico.events;

public class Choice {

    private final String label;
    private final Effects effects;

    public Choice(String label, Effects effects) {
        this.label = label;
        this.effects = effects;
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
