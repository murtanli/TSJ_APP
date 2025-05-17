package com.example.tsj_app.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.auto_booking.api.api_resource
import com.example.tsj_app.LoginActivity
import com.example.tsj_app.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val button_exit = binding.logoutButton

        button_exit.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("user_id")
            editor.remove("profile_id")
            editor.remove("user_name")
            editor.remove("count_meters")
            editor.putString("login", "false")
            editor.apply()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        // Показать прогресс-бар
        binding.progressBar.visibility = View.VISIBLE

        // Получаем данные из sharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("myPreferences", android.content.Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "")
        val id = userId?.toIntOrNull()

        if (userId != null) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val data = api_resource()
                    val profile = data.Get_profile_info(id)

                    // Устанавливаем данные в интерфейсе
                    binding.userFullName.text = "${profile.profile.first_name} ${profile.profile.last_name} ${profile.profile.surname}"
                    binding.userEmail.text = profile.profile.email
                    binding.userAddress.text = profile.profile.address
                    binding.userPhone.text = profile.profile.phone

                } catch (e: Exception) {
                    e.printStackTrace()

                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
