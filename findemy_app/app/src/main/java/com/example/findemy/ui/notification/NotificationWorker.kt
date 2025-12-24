package com.example.findemy.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.findemy.data.model.Event
import com.example.findemy.data.model.Jadwal
import com.example.findemy.data.model.Tugas
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit

// ============= Notification Worker =============
class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    companion object {
        const val CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_TYPE = "notification_type"
        const val NOTIFICATION_TITLE = "notification_title"
        const val NOTIFICATION_MESSAGE = "notification_message"
        const val NOTIFICATION_ID = "notification_id"
        const val JADWAL_ID = "jadwal_id"
        const val JADWAL_HARI = "jadwal_hari"
        const val JADWAL_JAM = "jadwal_jam"
        const val JADWAL_MATKUL = "jadwal_matkul"
        const val JADWAL_DOSEN = "jadwal_dosen"
        const val JADWAL_RUANGAN = "jadwal_ruangan"
    }

    override fun doWork(): Result {
        val type = inputData.getString(NOTIFICATION_TYPE) ?: return Result.failure()
        val title = inputData.getString(NOTIFICATION_TITLE) ?: return Result.failure()
        val message = inputData.getString(NOTIFICATION_MESSAGE) ?: return Result.failure()
        val notificationId = inputData.getInt(NOTIFICATION_ID, 0)

        createNotificationChannel()
        showNotification(title, message, notificationId)

        // Jika tipe jadwal, jadwalkan ulang untuk minggu depan
        if (type == "jadwal") {
            rescheduleJadwalNotification()
        }

        return Result.success()
    }

    private fun rescheduleJadwalNotification() {
        val jadwalId = inputData.getInt(JADWAL_ID, 0)
        val hari = inputData.getString(JADWAL_HARI) ?: return
        val jamMulai = inputData.getString(JADWAL_JAM) ?: return
        val matkul = inputData.getString(JADWAL_MATKUL) ?: ""
        val dosen = inputData.getString(JADWAL_DOSEN) ?: ""
        val ruangan = inputData.getString(JADWAL_RUANGAN) ?: ""

        val jadwal = Jadwal(
            id = jadwalId,
            user_id = 0,
            hari = hari,
            jam_mulai = jamMulai,
            jam_selesai = "",
            mata_kuliah = matkul,
            dosen = dosen,
            ruangan = ruangan,
            pasang_pengingat = true,
            created_at = "",
            updated_at = "",
        )

        NotificationScheduler.scheduleJadwalNotification(context, jadwal)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pengingat"
            val descriptionText = "Channel untuk pengingat jadwal, tugas, dan event"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String, notificationId: Int) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}

// ============= Notification Scheduler =============
object NotificationScheduler {

    private const val TAG_PREFIX = "notification_"
    private const val TYPE_JADWAL = "jadwal"
    private const val TYPE_TUGAS = "tugas"
    private const val TYPE_EVENT = "event"

    /**
     * Jadwalkan semua notifikasi (dipanggil setelah login atau update data)
     */
    fun scheduleAllNotifications(
        context: Context,
        jadwalList: List<Jadwal>,
        tugasList: List<Tugas>,
        eventList: List<Event>
    ) {
        // Batalkan semua notifikasi yang sudah dijadwalkan
        cancelAllNotifications(context)

        // Jadwalkan notifikasi baru
        jadwalList.forEach { if (it.pasang_pengingat) scheduleJadwalNotification(context, it) }
        tugasList.forEach { if (it.pasang_pengingat) scheduleTugasNotification(context, it) }
        eventList.forEach { if (it.pasang_pengingat) scheduleEventNotification(context, it) }
    }

    /**
     * Jadwalkan notifikasi untuk Jadwal (H-1 jam)
     * BERULANG SETIAP MINGGU
     */
    fun scheduleJadwalNotification(context: Context, jadwal: Jadwal) {
        val notificationTime = calculateJadwalNotificationTime(jadwal) ?: return
        val now = LocalDateTime.now()

        if (notificationTime.isBefore(now)) return // Lewati jika waktu sudah lewat

        val delay = java.time.Duration.between(now, notificationTime).toMillis()

        val data = workDataOf(
            NotificationWorker.NOTIFICATION_TYPE to TYPE_JADWAL,
            NotificationWorker.NOTIFICATION_TITLE to "Pengingat Jadwal",
            NotificationWorker.NOTIFICATION_MESSAGE to "${jadwal.mata_kuliah} - ${jadwal.dosen} di ${jadwal.ruangan} dalam 1 jam",
            NotificationWorker.NOTIFICATION_ID to generateNotificationId(TYPE_JADWAL, jadwal.id),
            // Data tambahan untuk reschedule
            NotificationWorker.JADWAL_ID to jadwal.id,
            NotificationWorker.JADWAL_HARI to jadwal.hari,
            NotificationWorker.JADWAL_JAM to jadwal.jam_mulai,
            NotificationWorker.JADWAL_MATKUL to jadwal.mata_kuliah,
            NotificationWorker.JADWAL_DOSEN to jadwal.dosen,
            NotificationWorker.JADWAL_RUANGAN to jadwal.ruangan
        )

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("${TAG_PREFIX}${TYPE_JADWAL}_${jadwal.id}")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    /**
     * Jadwalkan notifikasi untuk Tugas (H-1 jam sebelum deadline)
     * SEKALI SAJA
     */
    fun scheduleTugasNotification(context: Context, tugas: Tugas) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val deadline = LocalDateTime.parse(tugas.deadline, formatter)
        val notificationTime = deadline.minusHours(1)
        val now = LocalDateTime.now()

        if (notificationTime.isBefore(now)) return // Lewati jika waktu sudah lewat

        val delay = java.time.Duration.between(now, notificationTime).toMillis()

        val data = workDataOf(
            NotificationWorker.NOTIFICATION_TYPE to TYPE_TUGAS,
            NotificationWorker.NOTIFICATION_TITLE to "Pengingat Tugas",
            NotificationWorker.NOTIFICATION_MESSAGE to "${tugas.judul} - Deadline dalam 1 jam!",
            NotificationWorker.NOTIFICATION_ID to generateNotificationId(TYPE_TUGAS, tugas.id)
        )

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("${TAG_PREFIX}${TYPE_TUGAS}_${tugas.id}")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    /**
     * Jadwalkan notifikasi untuk Event (H-1 hari)
     * SEKALI SAJA
     */
    fun scheduleEventNotification(context: Context, event: Event) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val eventStart = LocalDateTime.parse(event.tanggal_mulai, formatter)
        val notificationTime = eventStart.minusDays(1)
        val now = LocalDateTime.now()

        if (notificationTime.isBefore(now)) return // Lewati jika waktu sudah lewat

        val delay = java.time.Duration.between(now, notificationTime).toMillis()

        val data = workDataOf(
            NotificationWorker.NOTIFICATION_TYPE to TYPE_EVENT,
            NotificationWorker.NOTIFICATION_TITLE to "Pengingat Event",
            NotificationWorker.NOTIFICATION_MESSAGE to "${event.judul} besok pukul ${eventStart.format(DateTimeFormatter.ofPattern("HH:mm"))}",
            NotificationWorker.NOTIFICATION_ID to generateNotificationId(TYPE_EVENT, event.id)
        )

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("${TAG_PREFIX}${TYPE_EVENT}_${event.id}")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    /**
     * Hitung waktu notifikasi untuk jadwal (H-1 jam sebelum jam mulai)
     * Mencari jadwal berikutnya berdasarkan hari dalam seminggu
     */
    private fun calculateJadwalNotificationTime(jadwal: Jadwal): LocalDateTime? {
        val dayOfWeek = mapHariToDayOfWeek(jadwal.hari) ?: return null
        val time = LocalTime.parse(jadwal.jam_mulai, DateTimeFormatter.ofPattern("HH:mm"))

        val now = LocalDateTime.now()
        var nextOccurrence = now.with(TemporalAdjusters.nextOrSame(dayOfWeek)).with(time)

        // Jika waktu hari ini sudah lewat, ambil minggu depan
        if (nextOccurrence.isBefore(now) || nextOccurrence.isEqual(now)) {
            nextOccurrence = nextOccurrence.plusWeeks(1)
        }

        return nextOccurrence.minusHours(1)
    }

    /**
     * Map nama hari ke DayOfWeek
     */
    private fun mapHariToDayOfWeek(hari: String): DayOfWeek? {
        return when (hari.lowercase()) {
            "senin" -> DayOfWeek.MONDAY
            "selasa" -> DayOfWeek.TUESDAY
            "rabu" -> DayOfWeek.WEDNESDAY
            "kamis" -> DayOfWeek.THURSDAY
            "jumat" -> DayOfWeek.FRIDAY
            "sabtu" -> DayOfWeek.SATURDAY
            "minggu" -> DayOfWeek.SUNDAY
            else -> null
        }
    }

    /**
     * Generate unique notification ID
     */
    private fun generateNotificationId(type: String, id: Int): Int {
        return when (type) {
            TYPE_JADWAL -> 1000 + id
            TYPE_TUGAS -> 2000 + id
            TYPE_EVENT -> 3000 + id
            else -> id
        }
    }

    /**
     * Batalkan notifikasi berdasarkan tipe dan ID
     */
    fun cancelNotification(context: Context, type: String, id: Int) {
        val tag = "${TAG_PREFIX}${type}_${id}"
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }

    /**
     * Batalkan semua notifikasi
     */
    fun cancelAllNotifications(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(TAG_PREFIX)
    }
}

// ============= Contoh Penggunaan =============
/*
// 1. Di LoginScreen setelah login berhasil:
result.onSuccess { data ->
    val token = data.token
    val name = data.user?.name
    val mail = data.user?.email

    if (token != null && name != null && mail != null) {
        prefs.saveUserData(token, name, mail)

        // Fetch dan jadwalkan notifikasi setelah login
        scope.launch {
            try {
                val jadwalResult = repoJadwal.getJadwals()
                val tugasResult = repoTugas.getTugass()
                val eventResult = repoEvent.getEvents()

                val jadwalList = jadwalResult.getOrNull()?.data ?: emptyList()
                val tugasList = tugasResult.getOrNull()?.data ?: emptyList()
                val eventList = eventResult.getOrNull()?.data ?: emptyList()

                NotificationScheduler.scheduleAllNotifications(
                    context = context,
                    jadwalList = jadwalList,
                    tugasList = tugasList,
                    eventList = eventList
                )
            } catch (e: Exception) {
                android.util.Log.e("LoginScreen", "Error scheduling notifications", e)
            }
        }

        navController.navigate("base") {
            popUpTo("login") { inclusive = true }
        }
    }
}

// 2. Setelah Create/Update Jadwal:
fun onJadwalChanged(context: Context, jadwal: Jadwal) {
    NotificationScheduler.cancelNotification(context, "jadwal", jadwal.id)
    if (jadwal.pasang_pengingat) {
        NotificationScheduler.scheduleJadwalNotification(context, jadwal)
    }
}

// 3. Setelah Delete Jadwal:
fun onJadwalDeleted(context: Context, jadwalId: Int) {
    NotificationScheduler.cancelNotification(context, "jadwal", jadwalId)
}
*/