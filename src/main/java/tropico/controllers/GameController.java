package tropico.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import tropico.Faction;
import tropico.GameState;
import tropico.Player;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameController {

    @FXML
    private VBox factions;

    @FXML
    private Label farming;

    @FXML
    private Label industry;

    @FXML
    private Label foodUnit;

    @FXML
    private Label treasury;

    private GameState gameStates;

    @FXML
    public void initialize() throws FileNotFoundException {
        gameStates = new GameState();
        initFactions(gameStates.getPlayer(), factions);
        setPlayer(gameStates.getPlayer());
    }

    private void setPlayer(Player player) {
        setFactions(player, factions);
        setResources(player);
    }

    private void setResources(Player player) {
        treasury.setText("Trésorerie : " + player.getTreasury() + "%");
        foodUnit.setText("Nourriture : " + player.getFoodUnit());
        industry.setText("Industrialisation : " + player.getIndustry() + "%");
        farming.setText("Agriculture : " + player.getFarming() + "%");
    }

    private static void initFactions(Player player, Pane pane) {
        var children = pane.getChildren();
        children.clear();
        children.addAll(player.getFactions()
                .stream().map(f -> new Label(f.toString()))
                .collect(Collectors.toList()));
    }

    private static void setFactions(Player player, Pane pane) {
        var faction = player.getFactions();
        var children = pane.getChildren();

        List<Pair<Label, Faction>> pairs = IntStream.range(0, faction.size())
                .mapToObj(i -> new Pair<>((Label)children.get(i), faction.get(i)))
                .collect(Collectors.toList());

        pairs.forEach((p) -> p.getKey().setText(p.getValue().toString()));
    }

}
