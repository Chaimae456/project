package com.example.carpartsalpha.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.FragmentDashboardBinding
import com.example.carpartsalpha.ui.activities.SettingsActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       // val dashboardViewModel =  ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
            textView.text = "Welcome to the dashboard"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //  Override the onCreateOptionMenu function and inflate the Dashboard menu file init.
    // START
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    // END

    //  Override the onOptionItemSelected function and handle the action items init.
    // START
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.navigation_setting -> {

                // Launch the SettingActivity on click of action item.
                // START
                startActivity(Intent(activity, SettingsActivity::class.java))
                // END
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    // END

}