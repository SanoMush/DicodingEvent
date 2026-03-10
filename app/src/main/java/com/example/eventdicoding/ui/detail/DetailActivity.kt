package com.example.eventdicoding.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.data.local.FavoriteEventEntity
import com.example.eventdicoding.data.local.FavoriteEventRepository
import com.example.eventdicoding.data.response.ListEventsItem
import com.example.eventdicoding.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var fabFavorite: FloatingActionButton
    private var isFavorite = false
    private lateinit var favoriteEventRepository: FavoriteEventRepository

    companion object {
        const val EVENT_KEY = "event"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoriteEventRepository = FavoriteEventRepository.getInstance(this)
        fabFavorite = binding.fabFav

        val event = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EVENT_KEY, ListEventsItem::class.java)
                ?: intent.getParcelableExtra(EVENT_KEY, FavoriteEventEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EVENT_KEY) as? ListEventsItem
                ?: intent.getParcelableExtra(EVENT_KEY) as? FavoriteEventEntity
        }

        event?.let {
            when (it) {
                is ListEventsItem -> setupUIWithListEventsItem(it)
                is FavoriteEventEntity -> setupUIWithFavoriteEventEntity(it)
            }

            binding.btnDetailSign.setOnClickListener {
                val linkUrl = when (event) {
                    is ListEventsItem -> event.link
                    is FavoriteEventEntity -> event.link
                    else -> null
                }

                if (!linkUrl.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
                    startActivity(intent)
                } else {
                    Snackbar.make(binding.root, "Link pendaftaran tidak tersedia", Snackbar.LENGTH_SHORT).show()
                }
            }

            val eventId = when (event) {
                is ListEventsItem -> event.id.toString()
                is FavoriteEventEntity -> event.id
                else -> null
            }

            eventId?.let { checkFavoriteStatus(it) }

            fabFavorite.setOnClickListener {
                isFavorite = !isFavorite
                if (event is ListEventsItem) {
                    saveFavoriteStatus(event)
                } else if (event is FavoriteEventEntity) {
                    saveFavoriteStatusFromEntity(event)
                }
                updateFavoriteIcon()
            }
        } ?: run {
            Snackbar.make(binding.root, "Event tidak ditemukan", Snackbar.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupUIWithListEventsItem(event: ListEventsItem) {
        with(binding) {
            tvDetailName.text = event.name
            tvDetailOwnername.text = event.ownerName
            tvDetailBegintime.text = event.beginTime
            tvDetailQuota.text = getString(
                R.string.quota_left,
                event.quota?.minus(event.registrants ?: 0)
            )
            tvDetailDescription.text = event.description?.let {
                HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } ?: ""
            ivImageUpcoming.loadImage(event.imageLogo ?: event.mediaCover)
        }
    }

    private fun setupUIWithFavoriteEventEntity(event: FavoriteEventEntity) {
        with(binding) {
            tvDetailName.text = event.name
            tvDetailOwnername.text = event.ownerName
            tvDetailBegintime.text = event.beginTime
            tvDetailQuota.text = getString(
                R.string.quota_left,
                event.quota - event.registrants
            )
            tvDetailDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            ivImageUpcoming.loadImage(event.imageLogo)
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            fabFavorite.setImageResource(R.drawable.ic_unfavorite_black_24dp)
        }
    }

    private fun checkFavoriteStatus(eventId: String) {
        favoriteEventRepository.getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
            isFavorite = favoriteEvent != null
            updateFavoriteIcon()
        }
    }

    private fun saveFavoriteStatus(event: ListEventsItem) {
        if (isFavorite) {
            val favoriteEventEntity = FavoriteEventEntity(
                id = event.id.toString(),
                name = event.name ?: "",
                ownerName = event.ownerName ?: "",
                beginTime = event.beginTime ?: "",
                quota = event.quota ?: 0,
                registrants = event.registrants ?: 0,
                description = event.description ?: "",
                imageLogo = event.imageLogo ?: event.mediaCover ?: "",
                link = event.link ?: ""
            )
            favoriteEventRepository.insertEvent(favoriteEventEntity)
        } else {
            favoriteEventRepository.getFavoriteEventById(event.id.toString()).observe(this) { favoriteEvent ->
                favoriteEvent?.let {
                    favoriteEventRepository.delete(it)
                }
            }
        }
    }

    private fun saveFavoriteStatusFromEntity(event: FavoriteEventEntity) {
        if (isFavorite) {
            favoriteEventRepository.insertEvent(event)
        } else {
            favoriteEventRepository.getFavoriteEventById(event.id).observe(this) { favoriteEvent ->
                favoriteEvent?.let {
                    favoriteEventRepository.delete(it)
                }
            }
        }
    }
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .into(this)
}