package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val language by viewModel.currentLanguage.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()

  val context = LocalContext.current
  var photoUri by remember { mutableStateOf<Uri?>(null) }
  
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    uri?.let { selectedUri ->
      viewModel.saveProfilePhoto(context, selectedUri)
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("profile_screen"),
    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable {
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            },
          contentAlignment = Alignment.Center
        ) {
          val photoPath = userProfile.profilePhotoPath
          if (!photoPath.isNullOrEmpty()) {
            AsyncImage(
              model = ImageRequest.Builder(context)
                .data(photoPath)
                .crossfade(true)
                .build(),
              contentDescription = "Profile photo",
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
              contentScale = ContentScale.Crop
            )
          } else {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(32.dp)
            )
          }
          
          // Camera icon overlay
          Surface(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .size(18.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "Change photo",
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.padding(2.dp)
            )
          }
        }

        Column {
          Text(
            text = AppStrings.profile(language),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      }
    }

    // Section 1: Preferences & Language
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = AppStrings.language(language),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Bilingual selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.setLanguage(Language.SPANISH) }
                .testTag("lang_es_btn"),
              shape = RoundedCornerShape(12.dp),
              color = if (language == Language.SPANISH) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
              Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                if (language == Language.SPANISH) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                  text = "Español",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = if (language == Language.SPANISH) Color.White else MaterialTheme.colorScheme.onSurface
                )
              }
            }

            Surface(
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.setLanguage(Language.ENGLISH) }
                .testTag("lang_en_btn"),
              shape = RoundedCornerShape(12.dp),
              color = if (language == Language.ENGLISH) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
              Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                if (language == Language.ENGLISH) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                  text = "English",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = if (language == Language.ENGLISH) Color.White else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
          Spacer(modifier = Modifier.height(12.dp))

          // Dark Mode Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = AppStrings.darkMode(language),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Switch(
              checked = userProfile.isDarkTheme == true,
              onCheckedChange = { isChecked ->
                viewModel.setThemeMode(isChecked)
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ProfileNavigationRow(
  icon: ImageVector,
  title: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.outline,
        modifier = Modifier.size(20.dp)
      )
      Text(
        text = title,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    Icon(
      imageVector = Icons.Default.ArrowForwardIos,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.outlineVariant,
      modifier = Modifier.size(14.dp)
    )
  }
}
