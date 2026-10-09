package com.greader.neo.widget

import androidx.glance.color.ColorProviders
import com.greader.neo.ui.theme.DarkColors
import com.greader.neo.ui.theme.LightColors

/**
 * Cores dos widgets: usa a paleta da marca do app (claro/escuro),
 * garantindo par fundo/texto consistente e contraste adequado.
 */
val WidgetColors: ColorProviders = androidx.glance.material3.ColorProviders(
    LightColors,
    DarkColors,
)
