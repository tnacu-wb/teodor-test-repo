package com.whitbread.premierinn.common.activity;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DialogMakerTest {

    @Mock
    Context contextMock;
    @Mock
    DialogMaker.AlertDialogBuilderFactory alertDialogBuilderFactoryMock;
    @Mock
    AlertDialog.Builder alertDialogBuilderMock;
    private DialogMaker dialogMaker;

    @Before
    public void onSetup() {
        dialogMaker = new DialogMaker(alertDialogBuilderFactoryMock);
    }

    @Test
    public void testShowDialog() {
        String title = "Test title";
        String description = "Test Description";
        when(alertDialogBuilderFactoryMock.getAlertDialogBuilder(contextMock)).thenReturn(alertDialogBuilderMock);
        when(alertDialogBuilderMock.setTitle(title)).thenReturn(alertDialogBuilderMock);
        when(alertDialogBuilderMock.setMessage(description)).thenReturn(alertDialogBuilderMock);
        when(alertDialogBuilderMock.setPositiveButton(eq(android.R.string.ok),
                any(DialogInterface.OnClickListener.class))).thenReturn(alertDialogBuilderMock);
        when(alertDialogBuilderMock.setIcon(android.R.drawable.ic_dialog_alert)).thenReturn(alertDialogBuilderMock);
        dialogMaker.showDialogAlertOk(contextMock, title, description);

        verify(alertDialogBuilderMock).setTitle(title);
        verify(alertDialogBuilderMock).setMessage(description);
        verify(alertDialogBuilderMock).setPositiveButton(eq(android.R.string.ok), any(DialogInterface.OnClickListener.class));
        verify(alertDialogBuilderMock).setIcon(android.R.drawable.ic_dialog_alert);
        verify(alertDialogBuilderMock).show();
    }


}
