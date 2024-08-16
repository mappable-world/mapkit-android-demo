package world.mappable.mapkitdemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import world.mappable.mapkit.MapKitFactory;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize the library to load required native libraries.
        // Warning! It's heavy operation
        MapKitFactory.initialize(this);
    }
}
