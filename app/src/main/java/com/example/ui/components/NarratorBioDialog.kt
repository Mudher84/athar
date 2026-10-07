package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.NarratorWithCount
import com.example.ui.theme.AtharColors
import com.example.ui.theme.AtharShapes
import com.example.ui.theme.AtharType
import com.example.ui.theme.palette
import com.example.util.arabicDigits

/**
 * حوار ترجمة الراوي (DESIGN_SPEC §4.9) بما في القاعدة فقط: الاسم والشهرة والبلد والطبقة
 * وعدد مروياته هنا. لا مولد ولا وفاة ولا أقوال جرح وتعديل، لأنها غير موجودة في البيانات بعد،
 * ولا يجوز اختلاقها.
 */
@Composable
fun NarratorBioDialog(
    narrator: NarratorWithCount,
    onDismiss: () -> Unit,
    onShowHadiths: (NarratorWithCount) -> Unit
) {
    val n = narrator.narrator
    val region = narrator.region
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Rtl {
            Column(
                Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(AtharShapes.Dialog)
                    .background(AtharColors.Ivory)
                    .verticalScroll(rememberScrollState())
            ) {
                // الترويسة الخضراء بحدّ عنبري سفلي
                Column(Modifier.fillMaxWidth().background(AtharColors.Cypress)) {
                    Column(Modifier.padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                Modifier
                                    .height(28.dp)
                                    .clip(AtharShapes.RegionBadge)
                                    .background(Color.White.copy(alpha = 0.14f))
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                RegionDot(AtharColors.OnCypressDot)
                                Text(region.label, style = AtharType.Badge, color = Color.White)
                            }
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Rounded.Close, contentDescription = "إغلاق", tint = Color.White)
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(n.popularName ?: n.name, style = AtharType.ScreenTitle, color = Color.White)
                        if (n.popularName != null && n.popularName != n.name) {
                            Text(n.name, style = AtharType.Lineage, color = AtharColors.OnCypressMuted)
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(3.dp).background(AtharColors.Amber))
                }

                // شريط الحقائق: ما في القاعدة فقط
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .background(AtharColors.Surface)
                        .padding(vertical = 12.dp)
                ) {
                    Fact("البلد", n.region, Modifier.weight(1f))
                    FactDivider()
                    Fact("الطبقة", n.era, Modifier.weight(1f))
                    FactDivider()
                    Fact("مروياته هنا", narrator.hadithCount.arabicDigits(), Modifier.weight(1f))
                }

                Column(
                    Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("في الموسوعة", style = AtharType.Link, color = AtharColors.AmberDeep)
                    Text(
                        "له ${narrator.hadithCount.arabicDigits()} رواية في هذه الموسوعة يرد فيها ضمن رواة المسار " +
                            "(${region.label}).",
                        style = AtharType.Body,
                        color = AtharColors.Ink
                    )
                    n.notes?.takeIf { it.isNotBlank() }?.let {
                        Text("ملاحظات", style = AtharType.Link, color = AtharColors.AmberDeep)
                        Text(it, style = AtharType.Body, color = AtharColors.Ink)
                    }
                    Text(
                        "المولد والوفاة وأقوال أئمة الجرح والتعديل لم تُدرَج في القاعدة بعد.",
                        style = AtharType.Secondary,
                        color = AtharColors.Muted
                    )
                    PrimaryButton("عرض آثاره في الموسوعة", onClick = { onShowHadiths(narrator) })
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    ) {
                        Text("إغلاق", style = AtharType.ButtonSmall, color = AtharColors.Cypress)
                    }
                }
            }
        }
    }
}

@Composable
private fun Fact(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = AtharType.Secondary, color = AtharColors.Muted)
        Text(value, style = AtharType.FactValue, color = AtharColors.Ink, textAlign = TextAlign.Center)
    }
}

@Composable
private fun FactDivider() {
    Box(Modifier.width(1.dp).fillMaxHeight().background(AtharColors.OutlineVariant))
}
