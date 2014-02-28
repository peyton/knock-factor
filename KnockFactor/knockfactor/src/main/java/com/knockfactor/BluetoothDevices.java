package com.knockfactor;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.knockfactor2.R;

import java.util.Set;

public class BluetoothDevices extends Activity implements AdapterView.OnItemClickListener {

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<BluetoothDevice> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        final Context context = this;

        mArrayAdapter = new ArrayAdapter<BluetoothDevice>(this, R.layout.list_item_device) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item_device, parent, false);
                }

                BluetoothDevice device = mArrayAdapter.getItem(position);

                TextView text = (TextView) convertView.findViewById(R.id.device_name);
                text.setText(device.getName());

                TextView mac = (TextView) convertView.findViewById(R.id.device_mac);
                mac.setText(device.getAddress());

                return convertView;
            }
        };

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Device does not support bluetooth", Toast.LENGTH_LONG);
        } else {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    mArrayAdapter.add(device);
                }
            }
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(mArrayAdapter))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bluetooth_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent();
        intent.putExtra(AuthenticatorActivity.EXTRA_SELECTED, mArrayAdapter.getItem(position).getAddress());

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ArrayAdapter<BluetoothDevice> list;

        public PlaceholderFragment(ArrayAdapter<BluetoothDevice> list) {
            this.list = list;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_bluetooth_devices, container, false);

            ListView devices = (ListView) rootView.findViewById(R.id.devices);
            devices.setAdapter(list);
            devices.setOnItemClickListener((AdapterView.OnItemClickListener) getActivity());

            return rootView;
        }
    }

}
