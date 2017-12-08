package com.example.q_thjen.mmessage.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.q_thjen.mmessage.Fragment.ChatsFragment;
import com.example.q_thjen.mmessage.Fragment.FriendsFragment;
import com.example.q_thjen.mmessage.Fragment.RequestFragment;

public class ViewPaperAdapter extends FragmentPagerAdapter {

    /** title to tablayout **/
    private String request, chat, friends;

    public ViewPaperAdapter(FragmentManager fm, String mrequest, String mchat, String mfriends) {
        super(fm);

        request = mrequest;
        chat = mchat;
        friends = mfriends;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;

            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default: return null;

        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {

        switch ( position ) {

            case 0: return request;
            case 1: return chat;
            case 2: return friends;

            default: return null;

        }

    }

}
