package com.gorkem.githubrepo.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.gorkem.githubrepo.R
import com.gorkem.githubrepo.base.BaseActivity
import com.gorkem.githubrepo.base.whenNonNull
import com.gorkem.githubrepo.data.local.favourite.Favourite
import com.gorkem.githubrepo.data.model.GithubRepoResponse
import com.gorkem.githubrepo.databinding.ActivityGithubRepoDetailBinding
import javax.inject.Inject


class GithubRepoDetailActivity :
    BaseActivity<ActivityGithubRepoDetailBinding, GithubRepoDetailViewModel>() {

    @Inject
    lateinit var viewModel: GithubRepoDetailViewModel

    private var repo: GithubRepoResponse? = null

    private var defaultFavourite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        repo = intent.extras?.getParcelable(REPO)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        repo.whenNonNull {
            supportActionBar!!.title = repo!!.name
            binding.item = repo!!
            defaultFavourite = repo!!.favourite

            binding.ivStar.setOnClickListener {
                if (repo!!.favourite) {
                    viewModel.deleteFavourite(Favourite(repo!!.id))
                } else {
                    viewModel.insertFavourite(Favourite(repo!!.id))
                }
                repo!!.favourite = !(repo!!.favourite)
                binding.item = repo!!
            }
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

    }

    override fun onBackPressed() {
        if (defaultFavourite != repo?.favourite) {
            val returnIntent = Intent()
            returnIntent.putExtra(REPO, repo)
            setResult(Activity.RESULT_OK, returnIntent)
        }
        finish()
    }

    override val layoutRes: Int
        get() = R.layout.activity_github_repo_detail
    override val vm: GithubRepoDetailViewModel
        get() = viewModel

    companion object {
        const val REPO = "REPO"
    }

}