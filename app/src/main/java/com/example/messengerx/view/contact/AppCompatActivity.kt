package com.example.messengerx.view.contact

import android.Manifest
import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messengerx.R

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Разрешение предоставлено, получаем контакты
                loadContacts()
            } else {
                // Разрешение отклонено
                Toast.makeText(this, "Доступ к контактам отклонен", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ContactsActivity)

        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        when {
            checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                // Разрешение уже предоставлено
                loadContacts()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                // Показать объяснение, почему нужно разрешение
                Toast.makeText(this, "Нужно разрешение для чтения контактов", Toast.LENGTH_LONG).show()
            }
            else -> {
                // Запрашиваем разрешение
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun loadContacts() {
        val resolver: ContentResolver = contentResolver
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                // Добавьте логику обработки контактов
                println("Имя: $name, Телефон: $phoneNumber")
            }
        }
    }
}
