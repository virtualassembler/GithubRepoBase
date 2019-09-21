package com.gorkem.githubrepo.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorkem.githubrepo.R
import com.gorkem.githubrepo.base.BaseActivity
import com.gorkem.githubrepo.base.SlientLoadingResourceImpl
import com.gorkem.githubrepo.base.launchActivity
import com.gorkem.githubrepo.base.whenNonNull
import com.gorkem.githubrepo.data.model.GithubRepoResponse
import com.gorkem.githubrepo.databinding.ActivityGithubRepoListBinding
import com.gorkem.githubrepo.ui.detail.GithubRepoDetailActivity
import com.gorkem.githubrepo.ui.detail.GithubRepoDetailActivity.Companion.REPO
import javax.inject.Inject


class GithubRepoListActivity :
    BaseActivity<ActivityGithubRepoListBinding, GithubRepoListViewModel>() {

    private var adapter = GithupRepoListAdapter()

    @Inject
    lateinit var viewModel: GithubRepoListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@GithubRepoListActivity)
            itemAnimator = DefaultItemAnimator()
        }
        binding.recyclerView.adapter = adapter
        adapter.listen().observe(this) {
            val repo = adapter.getItems()[it]
            launchActivity<GithubRepoDetailActivity>(DETAIL_REQUEST) {
                putExtra(REPO, (repo as GithubRepoResponse))
            }
        }

        binding.btnSearch.setOnClickListener {
            binding.etUserName.text.whenNonNull {
                requestRepoList(this.toString())
            }
        }
    }

    private fun requestRepoList(user: String) {
        if (user.isNotEmpty()) {
            call(viewModel.getRepoList(user),
                object : SlientLoadingResourceImpl<List<GithubRepoResponse>>(this) {
                    override fun onSuccess(data: List<GithubRepoResponse>?) {
                        adapter.addItems(data!!)
                    }

                    override fun onShowLoading() {
                        adapter.showLoading()
                    }

                    override fun onHideLoading(isSucces: Boolean) {
                        if (!isSucces) {
                            adapter.clearItems()
                            adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onError(message: String?) {
                        adapter.clearItems()
                        adapter.notifyDataSetChanged()
                        super.onError(message)
                    }
                })
        } else {
            onError(getString(R.string.search_repo_empty))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DETAIL_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                data.whenNonNull {
                    val result = data!!.getParcelableExtra<GithubRepoResponse?>(REPO)
                    result.whenNonNull { adapter.updateRepoStar(result!!.id, result.favourite) }
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override val layoutRes: Int
        get() = R.layout.activity_github_repo_list
    override val vm: GithubRepoListViewModel
        get() = viewModel

    companion object {
        public const val DETAIL_REQUEST = 1001
    }
}
