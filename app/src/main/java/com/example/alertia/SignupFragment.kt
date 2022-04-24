package com.example.alertia

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.alertia.databinding.FragmentSignupBinding


class SignupFragment : Fragment() {

    private lateinit var _binding : FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(layoutInflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.loginTV.setOnClickListener {
            val navController = Navigation.findNavController(requireView())
            navController.navigate(R.id.action_signupFragment_to_loginFragment)
        }

        _binding.regButton.setOnClickListener {
            val intent = Intent(context , MapsActivity::class.java)
            startActivity(intent)
            (activity as WelcomeActivity).finish()
        }
    }
}