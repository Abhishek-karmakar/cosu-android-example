package com.abhishek_karmakar.cosu_sample;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private DevicePolicyManager mDevicePolicyManager;
    private PackageManager mPackageManager;


    //TODO: later add this to check the state of the lock.
    private Boolean boolLockState;

    // add a variable to keep the component name in the application.
    private ComponentName mAdminComponentName;

    public static final String LOCK_ACTIVITY_KEY = "lock_activity";
    public static final int FROM_LOCK_ACTIVITY = 1;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set default COSU policy
        mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        boolLockState = false;

        // Retrieve DeviceAdminReceiver ComponentName so we can make
        // device management api calls later
            //mAdminComponentName = DeviceAdminReceiver.getComponentName(this);

        // Retrieve Package Manager so that we can enable and
        // disable LockedActivity
            mPackageManager = this.getPackageManager();

        // call the methods to implement the device policies.
        mAdminComponentName = DeviceAdminReceiver.getComponentName(this);
        mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        if(mDevicePolicyManager.isDeviceOwnerApp(getPackageName()))
        {
            setDefaultCosuPolicies(true);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Application not set to device owner", Toast.LENGTH_SHORT).show();
        }

        // check that the activity is starting the correct lockedActvity.
        Intent intent = getIntent();

        if(intent.getIntExtra(MainActivity.LOCK_ACTIVITY_KEY,0) ==
                MainActivity.FROM_LOCK_ACTIVITY){
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName,getPackageName());
            mPackageManager.setComponentEnabledSetting(
                    new ComponentName(getApplicationContext(), MainActivity.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.start_lock)
        {

            // start the webview
            Intent webviewIntent = new Intent(getApplicationContext(),WebPayment.class);
            startActivity(webviewIntent);
            finish();


//            if(mDevicePolicyManager.isDeviceOwnerApp(getApplicationContext().getPackageName()))
//            {
//                Intent intentLock = new Intent(getApplicationContext(), MainActivity.class);
//                mPackageManager.setComponentEnabledSetting(
//                        new ComponentName(getApplicationContext(),
//                                MainActivity.class),
//                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                        PackageManager.DONT_KILL_APP);
//                startActivity(intentLock);
//                //finish();
//            }
//
//            //setup the locking of the device here.
//            if(mDevicePolicyManager.isLockTaskPermitted(getApplicationContext().getPackageName()))
//            {
//                Intent intentLock = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intentLock);
//                finish();
//            }

        } else if (id == R.id.stop_lock)
        {
            // unlock the device here.
            ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
            if(am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_LOCKED)
            {
                stopLockTask();
            }
            // set the policies to false and enable everything back.
            setDefaultCosuPolicies(false);
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();

        } else if (id == R.id.nav_share)
        {

        } else if (id == R.id.nav_send)
        {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // start the locked mode if the activity is not already started.

        /*TODO: Setup a boolean at the time of locking and check it everytime to make sure that
        unlocked state is not given*/

        if(mDevicePolicyManager.isLockTaskPermitted(this.getPackageName()))
        {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if(am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE)
            {
                // start the lock
                startLockTask();
            }
        }
    }

    // the below methods allow us to take advantage of the latest device management apis
    private void setDefaultCosuPolicies(boolean active) {
        //set user-restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);

        //Disable keyguard and status bar
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);

        //enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active);

        //set system update policy
        if (active)
        {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName, SystemUpdatePolicy.createWindowedInstallPolicy(60,120));
        }
        else
        {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,null);
        }

        //set this activity as a lock task package
        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,active?new String[]{getPackageName()}:new String[]{});

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if(active)
        {
            //set the cosu activity as home intent so that it is started on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(mAdminComponentName, intentFilter, new ComponentName(getPackageName(), MainActivity.class.getName()));
        }
        else
        {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(mAdminComponentName,getPackageName());
        }

    }


    private void setUserRestriction(String restriction, boolean disallow)
    {
        if(disallow)
        {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName, restriction);
        }
        else
        {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName, restriction);
        }
    }

    private void enableStayOnWhilePluggedIn(boolean enabled)
    {
        if(enabled)
        {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    Integer.toString(BatteryManager.BATTERY_PLUGGED_AC
                    | BatteryManager.BATTERY_PLUGGED_USB
                    | BatteryManager.BATTERY_PLUGGED_WIRELESS));
        }
        else
        {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    "0"
            );
        }

    }





}
