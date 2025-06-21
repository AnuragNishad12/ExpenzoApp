package com.example.expenzo.Ui.Fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.lifecycle.lifecycleScope
import com.example.expenzo.R
import com.example.expenzo.Utils.UserDataStore
import com.example.expenzo.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import java.util.logging.Handler


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userDataStore: UserDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize UserDataStore
        userDataStore = UserDataStore(requireContext())

        // Load user data when fragment is created
        loadUserData()

        // Setup click listeners
        setupClickListeners()

        return binding.root
    }

    private fun loadUserData() {
        // Use lifecycleScope to handle coroutines in fragment
        lifecycleScope.launch {
            try {
                val userData = userDataStore.getAllUsersData()

                if (userData != null && userData.size >= 5) {
                    // Data order: name, password, email, uniqueName, mobileNumber
                    binding.tvName.text = userData[0].ifEmpty { "Not Available" }
                    binding.tvEmail.text = userData[2].ifEmpty { "Not Available" }
                    binding.tvUsername.text = if (userData[3].isNotEmpty()) "@${userData[3]}" else "Not Available"
                    binding.tvMobile.text = userData[4].ifEmpty { "Not Available" }
                } else {
                    // Set default values if no data found
                    setDefaultUserData()
                }
            } catch (e: Exception) {
                // Handle error and set default values
                setDefaultUserData()
                Toast.makeText(requireContext(), "Error loading user data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setDefaultUserData() {
        binding.tvName.text = "Not Available"
        binding.tvEmail.text = "Not Available"
        binding.tvUsername.text = "Not Available"
        binding.tvMobile.text = "Not Available"
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            // Navigate to edit profile
            // You can replace this with your actual edit profile activity/fragment
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        }

        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun showDeleteAccountDialog() {
        val builder = AlertDialog.Builder(requireContext())

        // Create custom view for dialog
        val inflater: LayoutInflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_delete_account, null)
        builder.setView(dialogView)

        val dialog = builder.create()

        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

        btnYes.setOnClickListener {
            dialog.dismiss()
//            deleteAccount()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        // Make dialog background transparent and show
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

//    private fun deleteAccount() {
//        // Show loading dialog
//        val progressDialog = ProgressDialog(requireContext())
//        progressDialog.setMessage("Deleting account...")
//        progressDialog.setCancelable(false)
//        progressDialog.show()
//
//        // Delete account process
//        lifecycleScope.launch {
//            try {
//                // Clear all user data from DataStore
//                userDataStore.clearAllData()
//
//                // Simulate deletion delay
//                Handler(Looper.getMainLooper()).postDelayed({
//                    progressDialog.dismiss()
//
//                    // Show success message
//                    Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
//
//                    // Navigate to login/welcome screen
//                    // Replace with your actual login activity
//                    val intent = Intent(requireContext(), LoginActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                    requireActivity().finish()
//
//                }, 2000) // 2 second delay for demo
//
//            } catch (e: Exception) {
//                progressDialog.dismiss()
//                Toast.makeText(requireContext(), "Error deleting account", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}