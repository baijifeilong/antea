package io.github.baijifeilong.antea;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private SwitchCompat serviceSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        serviceSwitch = navigationView.getMenu().findItem(R.id.menu_switch_service).getActionView().findViewById(R.id.service_switch);

        final TestFragment testFragment = new TestFragment();
        final DebugFragment debugFragment = new DebugFragment();
        final SettingsFragment settingsFragment = new SettingsFragment();
        final HelpFragment helpFragment = new HelpFragment();
        final PasswordGeneratorFragment passwordGeneratorFragment = new PasswordGeneratorFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, helpFragment).commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_test:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, testFragment).commit();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.menu_debug:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, debugFragment).commit();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.menu_help:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, helpFragment).commit();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.menu_settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, settingsFragment).commit();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.menu_password_generator:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, passwordGeneratorFragment).commit();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.menu_switch_service:
                        serviceSwitch.performClick();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        navigationView.getHeaderView(0).findViewById(R.id.navigation_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        serviceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SwitchCompat) v).setChecked(!((SwitchCompat) v).isChecked());
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceSwitch.setChecked(isAccessibilityEnabled(this, BuildConfig.APPLICATION_ID + "/.AnteaService"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    public static boolean isAccessibilityEnabled(Context context, String id) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        for (AccessibilityServiceInfo service : am.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK)) {
            if (id.equals(service.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
