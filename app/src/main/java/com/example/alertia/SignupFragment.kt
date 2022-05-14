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
import com.example.alertia.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignupFragment : Fragment() {

    private lateinit var _binding : FragmentSignupBinding
    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mAuth = Firebase.auth
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
            signUpUser(_binding.emailRegInput.text.toString() , _binding.passwordRegInput.text.toString())
            Log.i("Person InfO" ,_binding.emailRegInput.text.toString()  + _binding.passwordRegInput.text.toString())
        }
    }

    private fun signUpUser(email:String , password:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val intent = Intent(context , MapsActivity::class.java)
                    startActivity(intent)
                    (activity as WelcomeActivity).finish()
                }
                else{
                    Toast.makeText(requireContext(), "Create User failed.",
                        Toast.LENGTH_SHORT).show()
                    Log.w("SignUp Failed", "createUserWithEmail:failure", it.exception)
                }
            }
    }
}