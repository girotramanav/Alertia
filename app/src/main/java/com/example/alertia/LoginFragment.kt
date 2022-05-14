package com.example.alertia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.alertia.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var _binding : FragmentLoginBinding
    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mAuth = Firebase.auth
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.loginButton.setOnClickListener {
            val navController = Navigation.findNavController(requireView())
            navController.navigate(R.id.action_loginFragment_to_signupFragment)
        }

        _binding.button.setOnClickListener {
            signInUser(_binding.emailLoginInput.text.toString() , _binding.passwordLogInput.text.toString())
        }
    }

    private fun signInUser(email:String , password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) {
                if(it.isSuccessful){
                    val intent = Intent(context , MapsActivity::class.java)
                    startActivity(intent)
                    (activity as WelcomeActivity).finish()
                }
                else{
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    Log.w("SignIn Failed", "SignInUserWithEmail:failure", it.exception)
                }
            }
    }

}