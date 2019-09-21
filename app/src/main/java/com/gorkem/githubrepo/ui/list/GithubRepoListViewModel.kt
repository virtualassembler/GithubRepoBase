package com.gorkem.githubrepo.ui.list

import com.gorkem.githubrepo.base.BaseViewModel
import com.gorkem.githubrepo.data.repository.GithupRepository
import javax.inject.Inject

class GithubRepoListViewModel @Inject constructor(var repository: GithupRepository) :
    BaseViewModel() {

    fun getRepoList(user: String) = call { repository.getRepoList(user) }

//    fun getFavourites() = callDBAndReturn{repository.getFavourites()}

}