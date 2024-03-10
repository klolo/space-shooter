package main.pl.klolo.spaceshooter;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import static pl.klolo.spaceshooter.game.engine.GameEngineFactoryKt.createGameEngine;
import pl.klolo.spaceshooter.game.engine.Profile;


public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = getAndroidConfiguration();
		initialize(createGameEngine(Profile.ANDROID), config);
	}

	private AndroidApplicationConfiguration getAndroidConfiguration() {
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		config.useCompass = false;
		return config;
	}
}
