package tropico.events;

import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.List;

public class Effects extends HashMap<String, Integer> {

    private static final List<String> available = List.of(
            "capitalistes",
            "communistes",
            "liberaux",
            "religieux",
            "militaristes",
            "ecologistes",
            "nationalistes",
            "loyalites",
            "agriculture",
            "industrialisation",
            "tresorerie"
    );

    public static boolean isAvailable(String effect) {
        return available.contains(effect);
    }

    @Override
    public Integer put(String effect, Integer value) {
        if (!Effects.isAvailable(effect)) throw new JsonSyntaxException("Effect " + effect + " is not available");
        return super.put(effect, value);
    }
}
