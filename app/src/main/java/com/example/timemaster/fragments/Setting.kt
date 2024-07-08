package com.example.timemaster.fragments

import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.timemaster.LoginPage
import com.example.timemaster.MainActivity
import com.example.timemaster.R
import com.example.timemaster.databinding.FragmentSettingBinding

class Setting : Fragment() {

    lateinit var binding: FragmentSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).binding.bottomMenuBar.menu.getItem(4).isChecked = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignOut.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireActivity())
            dialogBuilder.setMessage("Are you sure to signout")
                .setPositiveButton("Ok", { dialog, id ->
                    val sharedPreferences = requireActivity().getSharedPreferences(
                        "Register",
                        Context.MODE_PRIVATE
                    )
                    with(sharedPreferences.edit()){
                        remove("isLoggedIn")
                        apply()
                    }
                    startActivity(Intent(requireActivity(), LoginPage::class.java))
                    dialog.dismiss()

                })
                .setNegativeButton("Cancel", { dialog, id ->
                    dialog.dismiss()
                })

            val alert = dialogBuilder.create()
            dialogBuilder.setTitle("SignOut")
            alert.show()
        }
    }

}