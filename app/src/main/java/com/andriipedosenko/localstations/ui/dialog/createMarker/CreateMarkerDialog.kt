package com.andriipedosenko.localstations.ui.dialog.createMarker

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.andriipedosenko.localstations.databinding.DialogCreateMarkerBinding
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateMarkerDialog : DialogFragment() {

    companion object {
        fun show(
            latLng: LatLng,
            fragmentManager: FragmentManager,
            onMarkerCreated: () -> Unit
        ) {
            val dialog = CreateMarkerDialog()

            dialog.latLng = latLng
            dialog.onMarkerCreated = onMarkerCreated

            dialog.show(fragmentManager, dialog.tag)
        }
    }

    private var _binding: DialogCreateMarkerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateMarkerViewModel by viewModels()

    private var latLng: LatLng? = null
    private var onMarkerCreated: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateMarkerBinding.inflate(inflater, container, false)

        setupClickListeners()
        setupViewModelCallbacks()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun setupClickListeners() = with(binding) {
        saveButton.setOnClickListener {
            if (latLng == null) {
                showErrorToast()
                return@setOnClickListener
            }

            viewModel.createMarker(
                mcc = mccTextField.value,
                mnc = mncTextField.value,
                lac = lacTextField.value,
                psc = pscTextField.value,
                rat = ratTextField.value,
                latLng = latLng!!
            )
        }
    }

    private fun setupViewModelCallbacks() = with(viewModel){
        isCreatedSuccessfully.observe(viewLifecycleOwner) {
            onMarkerCreated?.invoke()
            dismiss()
        }

        isFailed.observe(viewLifecycleOwner) {
            showErrorToast()
        }
    }

    private fun showErrorToast() {
        Toast.makeText(requireContext(), "Try again", Toast.LENGTH_SHORT).show()
    }
}