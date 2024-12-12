package com.contexts.calllog

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.contexts.calllog.databinding.ActivityCallLogBinding
import kotlinx.coroutines.launch

class CallLogActivity : AppCompatActivity() {

    private val viewModel: CallLogViewModel by viewModels {
        CallLogViewModel.factory((application as CallLogApplication).repository)
    }
    private lateinit var binding: ActivityCallLogBinding
    private lateinit var adapter: CallLogAdapter
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.fetchCallLogs()
        } else {
            showError("Need permissions to get call logs")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCallLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        subscribeViewModel()
        checkPermissionAndFetchLogs()
    }

    private fun setupRecyclerView() {
        adapter = CallLogAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.hasFixedSize()
    }

    private fun checkPermissionAndFetchLogs() {
        when {
            isCallLogPermissionGranted() -> {
                viewModel.fetchCallLogs()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CALL_LOG) -> {
                showPermissionRationale()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_CALL_LOG)
            }
        }
    }

    private fun isCallLogPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED

    private fun subscribeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateUi(it)
                }
            }
        }
    }

    private fun updateUi(state: CallLogUiState) {
        binding.progressBar.isVisible = state.isLoading
        adapter.submitList(state.logs)
        state.error?.let { showError(it) }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Access to call logs is required to show your history. Would you like to grant this permission?")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissionLauncher.launch(android.Manifest.permission.READ_CALL_LOG)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}