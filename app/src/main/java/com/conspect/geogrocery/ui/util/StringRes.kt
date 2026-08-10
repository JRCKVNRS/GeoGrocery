package com.conspect.geogrocery.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/** Thin wrapper around [stringResource] so screens can call `stringRes(...)` uniformly. */
@Composable
fun stringRes(@StringRes id: Int, vararg formatArgs: Any): String =
    if (formatArgs.isEmpty()) stringResource(id) else stringResource(id, *formatArgs)
