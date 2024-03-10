package pl.klolo.spaceshooter;

import static pl.klolo.game.engine.GameEngineFactoryKt.createGameEngine;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.typesafe.config.Config;

import pl.klolo.game.engine.GameEngine;
import pl.klolo.game.engine.Profile;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        final GameEngine gameEngine = createGameEngine(Profile.DESKTOP);

        Lwjgl3ApplicationConfiguration appConfig = new Lwjgl3ApplicationConfiguration();
        Config applicationConfigFromFile = gameEngine.getConfig("application");
        appConfig.setTitle(applicationConfigFromFile.getString("title"));
        appConfig.setWindowedMode(
                applicationConfigFromFile.getInt("width"),
                applicationConfigFromFile.getInt("height")
        );

        // TODO: enable fullscreen based on: applicationConfigFromFile.getBoolean("fullscreen"));
        new Lwjgl3Application(gameEngine, appConfig);
    }
}
