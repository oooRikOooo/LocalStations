package com.andriipedosenko.localstations.ui.dialog.infoMarker

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.andriipedosenko.localstations.databinding.DialogInfoMarkerBinding
import com.andriipedosenko.localstations.domain.model.Station
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoMarkerDialog : DialogFragment() {
    companion object {
        fun show(
            station: Station,
            fragmentManager: FragmentManager,
            onMarkerUpdated: () -> Unit
        ) {
            val dialog = InfoMarkerDialog()

            dialog.station = station
            dialog.onMarkerUpdated = onMarkerUpdated

            dialog.show(fragmentManager, dialog.tag)
        }
    }

    private var _binding: DialogInfoMarkerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InfoMarkerViewModel by viewModels()

    private var station: Station? = null
    private var onMarkerUpdated: (() -> Unit)? = null

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
        _binding = DialogInfoMarkerBinding.inflate(inflater, container, false)

        setupTextFieldDefaults()
        setupClickListeners()
        setupViewModelCallbacks()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() = with(binding) {
        editButton.setOnClickListener {
            viewModel.updateMode(InfoMarkerDialogMode.Edit)
        }

        saveButton.setOnClickListener {
            if (station == null) {
                showErrorToast()
                return@setOnClickListener
            }

            viewModel.updateMarker(
                mcc = mccTextField.value,
                mnc = mncTextField.value,
                lac = lacTextField.value,
                psc = pscTextField.value,
                rat = ratTextField.value,
                cellId = station!!.cellId,
                latitude = station!!.latitude,
                longitude = station!!.longitude,
            )
        }

        cancelButton.setOnClickListener {
            setupTextFieldDefaults()
            viewModel.updateMode(InfoMarkerDialogMode.Info)
        }
    }

    private fun setupViewModelCallbacks() = with(viewModel) {
        isUpdatedSuccessfully.observe(viewLifecycleOwner) {
            onMarkerUpdated?.invoke()
            dismiss()
        }

        isFailed.observe(viewLifecycleOwner) {
            showErrorToast()
        }

        mode.observe(viewLifecycleOwner) {
            handleMode(it)
        }
    }

    private fun setupTextFieldDefaults() = with(binding) {
        mccTextField.value = station?.mcc.toString()
        mncTextField.value = station?.mnc.toString()
        lacTextField.value = station?.lac.toString()
        pscTextField.value = station?.psc.toString()
        ratTextField.value = station?.rat.toString()
    }

    private fun showErrorToast() {
        Toast.makeText(requireContext(), "Try again", Toast.LENGTH_SHORT).show()
    }

    private fun handleMode(infoMarkerDialogMode: InfoMarkerDialogMode) = with(binding){
        when (infoMarkerDialogMode) {
            InfoMarkerDialogMode.Info -> {
                editButton.isVisible = true
                editModeControls.isVisible = false

                mccTextField.isEnabled = false
                mncTextField.isEnabled = false
                lacTextField.isEnabled = false
                pscTextField.isEnabled = false
                ratTextField.isEnabled = false
            }

            InfoMarkerDialogMode.Edit -> {
                editButton.isVisible = false
                editModeControls.isVisible = true

                mccTextField.isEnabled = true
                mncTextField.isEnabled = true
                lacTextField.isEnabled = true
                pscTextField.isEnabled = true
                ratTextField.isEnabled = true
            }
        }
    }
}