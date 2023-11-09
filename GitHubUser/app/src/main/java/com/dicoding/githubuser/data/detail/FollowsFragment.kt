package com.dicoding.githubuser.data.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.data.response.ItemsItem
import com.dicoding.githubuser.databinding.FragmentFollowsBinding

class FollowsFragment : Fragment() {
    private var _binding: FragmentFollowsBinding? = null
    private var viewType: Int = FOLLOWERS_VIEW
    private val binding get() = _binding!!
    private val userViewModel: DetailViewModel by activityViewModels()

    private lateinit var followAdapter: RecyclerViewFollowAdapter
    private var username : String = ""

    private val followers: LiveData<List<ItemsItem>> by lazy {
        userViewModel.getFollowers()
    }
    private val following : LiveData<List<ItemsItem>> by lazy {
        userViewModel.getFollowing()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        username = arguments?.getString(ARG_USERNAME) ?: ""
        viewType = arguments?.getInt(ARG_VIEW_TYPE, FOLLOWERS_VIEW) ?: FOLLOWERS_VIEW

        if (viewType == FOLLOWERS_VIEW) {
            userViewModel.setFollowers(username)
        } else {
            userViewModel.setFollowing(username)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = arguments?.getString(ARG_USERNAME) ?: ""
        viewType = arguments?.getInt(ARG_VIEW_TYPE, FOLLOWERS_VIEW) ?: FOLLOWERS_VIEW

        initRecyclerView()

        when (viewType) {
            FOLLOWERS_VIEW -> {
                observeFollowers()
            }
            FOLLOWING_VIEW -> {
                observeFollowing()
            }
        }
    }

    private fun initRecyclerView() {
//        followAdapter = RecyclerViewFollowAdapter(
//            if (viewType == FOLLOWERS_VIEW) followers.value ?: emptyList()
//            else following.value ?: emptyList())
//        val recyclerView: RecyclerView = binding.rvFollow
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = followAdapter

        val followersList = if (viewType == FOLLOWERS_VIEW) {
            followers.value ?: emptyList()
        } else {
            following.value ?: emptyList()
        }

        followAdapter = RecyclerViewFollowAdapter(followersList)
        val recyclerView: RecyclerView = binding.rvFollow
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = followAdapter
    }

    private fun observeFollowers() {
        showLoading(true)
            val data = if (viewType == FOLLOWERS_VIEW) {
                userViewModel.getFollowers()
            } else{
                userViewModel.getFollowing()
            }

            data.observe(viewLifecycleOwner) { followers ->
                showLoading(false)
                binding.progressBar.visibility =View.GONE
                if (followers.isNotEmpty()) {
                    followAdapter.setList(followers)
                } else {
                    val message = if (viewType == FOLLOWERS_VIEW) {
                        "Tidak ada pengikut"
                    } else {
                        "Tidak ada yang diikuti"
                    }
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }

    }
    private fun observeFollowing() {
        showLoading(true)
        if (username.isNotBlank()) {
            val data = if (viewType == FOLLOWING_VIEW) {
                userViewModel.getFollowing()
            } else{
                userViewModel.getFollowers()
            }
            data.observe(viewLifecycleOwner) { following ->
                showLoading(false)
                binding.progressBar.visibility = View.GONE
                if (following.isNotEmpty()) {
                    followAdapter.setList(following)
                } else{
                    Toast.makeText(requireContext(), "Tidak ada yg diikuti", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(state: Boolean) { binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val ARG_USERNAME = "arg_username"
        const val ARG_VIEW_TYPE = "arg_view_type"
        const val FOLLOWERS_VIEW = 0
        const val FOLLOWING_VIEW =1

        fun newInstance( username:String, viewType: Int): FollowsFragment{
            val fragment = FollowsFragment()
            val args = Bundle().apply {
                putString(ARG_USERNAME, username)
                putInt(ARG_VIEW_TYPE, viewType)
            }
            fragment.arguments = args
            return fragment
        }
    }

}
