package com.andriipedosenko.localstations.ui.dialog.infoMarker

sealed class InfoMarkerDialogMode {
    data object Info : InfoMarkerDialogMode()
    data object Edit : InfoMarkerDialogMode()
}