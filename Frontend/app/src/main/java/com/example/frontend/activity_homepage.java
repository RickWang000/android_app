package com.example.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class activity_homepage extends AppCompatActivity {
    String username = "";
    String password = "";
    String nickname = "";
    String introduction = "";
    private String TESTSTRING1 = "username";
    private String TESTSTRING2 = "password";
    private String loginUsername = "";
    private String loginPassword = "";
    private final String LOGINSTATUS = "loginstatus";
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    boolean isLogin = false; // if True, restore the previous login status.
    private BottomNavigationView bottomNavigationView;
    public static user User;
    private RecyclerView mPostRecyclerView;
    private PostAdapter mPostAdapter;
    private static final int newPost=1;
    private List<Post> posts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        setContentView(R.layout.activity_homepage);
        Bundle bundle = this.getIntent().getExtras();
        username = mPreferences.getString("username", username);
        password = mPreferences.getString("password", password);
        nickname = mPreferences.getString("nickname", nickname);
        introduction = mPreferences.getString("introduction", introduction);
        isLogin = mPreferences.getBoolean("loginstatus", isLogin);
        // todo: create the User with params
        User=new user(1,username,password,nickname,introduction);
        // Log.d("a",User.getUsername());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        jumpToHomePage();
                        return true;
                    case R.id.navigation_topic:
                        selectedFragment = new BlankFragment();
                        break;
                    case R.id.navigation_guide: {
                        jumpToChat();
                        return true;
                    }
                    case R.id.navigation_me: {
                        jumpToUserInfo(); // 调用跳转到用户信息界面的方法
                        return true; // 注意要在此处返回 true，表示已处理点击事件
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
        // Find the RecyclerView and set its LayoutManager
        mPostRecyclerView = findViewById(R.id.post_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPostRecyclerView.setLayoutManager(layoutManager);

        // Create a list of Post objects and set the adapter
        posts = new ArrayList<>();
        // Populate the list with Post objects
        Post post1 = new Post();
        Post post2 = new Post();
        Post post3 = new Post();
        Post post4 = new Post();
        posts.add(post1);
        posts.add(post2);
        posts.add(post3);
        posts.add(post4);

        mPostAdapter = new PostAdapter(posts);
        mPostRecyclerView.setAdapter(mPostAdapter);

        mPostAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String viewType) {
                Intent intent = new Intent(activity_homepage.this, PostInfoActivity.class);
                intent.putExtra("post", posts.get(position));
                startActivity(intent);
            }
        });



        FloatingActionButton addPostButton = findViewById(R.id.add_post_button);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_homepage.this, activity_postedit.class);
                startActivityForResult(intent,newPost);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case newPost:
                if (resultCode == RESULT_OK) {
                    Post post=data.getParcelableExtra("newPost");
                    post.setAuthor(User.getUsername());
                    // todo: notify backend
                    posts.add(post);
                    mPostAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public void jumpToHomePage(){
        Intent intent=new Intent(this,activity_homepage.class);
        startActivity(intent);
    }
    public void jumpToUserInfo() {
        Intent intent = new Intent(this, activity_userinfo.class);
        startActivity(intent);
    }

    public void jumpToChat(){
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        Intent intent = new Intent(this, activity_chat.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @SuppressLint("ApplySharedPref")
    public void logout(View v) {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean(LOGINSTATUS, false); // login status should be false
        preferencesEditor.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    protected void onPause() {
        super.onPause();
    }
}