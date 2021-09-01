package com.example.navigation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDestination.ClassType;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.Navigator.Name;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.R.styleable;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ================================================
 * 类名：com.example.navigation.widget
 * 时间：2021/8/31 17:42
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 *
 * @author Admin
 */

@Name("hifragment")
public class HiFragmentNavigator extends Navigator<HiFragmentNavigator.Destination> {
    private static final String TAG = "FragmentNavigator";
    private static final String KEY_BACK_STACK_IDS = "androidx-nav-fragment:navigator:backStackIds";
    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final int mContainerId;
    private ArrayDeque<Integer> mBackStack = new ArrayDeque();

    public HiFragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        this.mContext = context;
        this.mFragmentManager = manager;
        this.mContainerId = containerId;
    }

    @Override
    public boolean popBackStack() {
        if (this.mBackStack.isEmpty()) {
            return false;
        } else if (this.mFragmentManager.isStateSaved()) {
            Log.i("FragmentNavigator", "Ignoring popBackStack() call: FragmentManager has already saved its state");
            return false;
        } else {
            this.mFragmentManager.popBackStack(this.generateBackStackName(this.mBackStack.size(), (Integer) this.mBackStack.peekLast()), 1);
            this.mBackStack.removeLast();
            return true;
        }
    }

    @Override
    @NonNull
    public Destination createDestination() {
        return new Destination(this);
    }

    /**
     * @deprecated
     */
    @Deprecated
    @NonNull
    public Fragment instantiateFragment(@NonNull Context context, @NonNull FragmentManager fragmentManager, @NonNull String className, @Nullable Bundle args) {
        return fragmentManager.getFragmentFactory().instantiate(context.getClassLoader(), className);
    }

    @Override
    @Nullable
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable androidx.navigation.Navigator.Extras navigatorExtras) {
        if (this.mFragmentManager.isStateSaved()) {
            Log.i("FragmentNavigator", "Ignoring navigate() call: FragmentManager has already saved its state");
            return null;
        } else {
            String className = destination.getClassName();
            if (className.charAt(0) == '.') {
                className = this.mContext.getPackageName() + className;
            }
            //android.fragment.app.homefragment homefragment
            String tag = className.substring(className.lastIndexOf('.') + 1);
            Fragment frag = mFragmentManager.findFragmentByTag(tag);
            if (frag == null) {
                frag = this.instantiateFragment(this.mContext, this.mFragmentManager, className, args);
            }

            frag.setArguments(args);
            FragmentTransaction ft = this.mFragmentManager.beginTransaction();
            int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
            int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
            int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
            int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
            if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
                enterAnim = enterAnim != -1 ? enterAnim : 0;
                exitAnim = exitAnim != -1 ? exitAnim : 0;
                popEnterAnim = popEnterAnim != -1 ? popEnterAnim : 0;
                popExitAnim = popExitAnim != -1 ? popExitAnim : 0;
                ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
            }

            List<Fragment> fragments = mFragmentManager.getFragments();
            for (Fragment fragment : fragments) {
                ft.hide(fragment);
            }
            if (!frag.isAdded()) {
                ft.add(mContainerId, frag, tag);
            }
            ft.show(frag);

//            ft.replace(this.mContainerId, frag);
            ft.setPrimaryNavigationFragment(frag);
            int destId = destination.getId();
            boolean initialNavigation = this.mBackStack.isEmpty();
            boolean isSingleTopReplacement = navOptions != null && !initialNavigation && navOptions.shouldLaunchSingleTop() && (Integer) this.mBackStack.peekLast() == destId;
            boolean isAdded;
            if (initialNavigation) {
                isAdded = true;
            } else if (isSingleTopReplacement) {
                if (this.mBackStack.size() > 1) {
                    this.mFragmentManager.popBackStack(this.generateBackStackName(this.mBackStack.size(), (Integer) this.mBackStack.peekLast()), 1);
                    ft.addToBackStack(this.generateBackStackName(this.mBackStack.size(), destId));
                }

                isAdded = false;
            } else {
                ft.addToBackStack(this.generateBackStackName(this.mBackStack.size() + 1, destId));
                isAdded = true;
            }

            if (navigatorExtras instanceof Extras) {
                Extras extras = (Extras) navigatorExtras;
                Iterator var17 = extras.getSharedElements().entrySet().iterator();

                while (var17.hasNext()) {
                    Entry<View, String> sharedElement = (Map.Entry) var17.next();
                    ft.addSharedElement((View) sharedElement.getKey(), (String) sharedElement.getValue());
                }
            }

            ft.setReorderingAllowed(true);
            ft.commit();
            if (isAdded) {
                this.mBackStack.add(destId);
                return destination;
            } else {
                return null;
            }
        }
    }

    @Override
    @Nullable
    public Bundle onSaveState() {
        Bundle b = new Bundle();
        int[] backStack = new int[this.mBackStack.size()];
        int index = 0;

        Integer id;
        for (Iterator var4 = this.mBackStack.iterator(); var4.hasNext(); backStack[index++] = id) {
            id = (Integer) var4.next();
        }

        b.putIntArray("androidx-nav-fragment:navigator:backStackIds", backStack);
        return b;
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        if (savedState != null) {
            int[] backStack = savedState.getIntArray("androidx-nav-fragment:navigator:backStackIds");
            if (backStack != null) {
                this.mBackStack.clear();
                int[] var3 = backStack;
                int var4 = backStack.length;

                for (int var5 = 0; var5 < var4; ++var5) {
                    int destId = var3[var5];
                    this.mBackStack.add(destId);
                }
            }
        }

    }

    @NonNull
    private String generateBackStackName(int backStackIndex, int destId) {
        return backStackIndex + "-" + destId;
    }

    public static final class Extras implements androidx.navigation.Navigator.Extras {
        private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap();

        Extras(Map<View, String> sharedElements) {
            this.mSharedElements.putAll(sharedElements);
        }

        @NonNull
        public Map<View, String> getSharedElements() {
            return Collections.unmodifiableMap(this.mSharedElements);
        }

        public static final class Builder {
            private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap();

            public Builder() {
            }

            @NonNull
            public Extras.Builder addSharedElements(@NonNull Map<View, String> sharedElements) {
                Iterator var2 = sharedElements.entrySet().iterator();

                while (var2.hasNext()) {
                    Entry<View, String> sharedElement = (Map.Entry) var2.next();
                    View view = (View) sharedElement.getKey();
                    String name = (String) sharedElement.getValue();
                    if (view != null && name != null) {
                        this.addSharedElement(view, name);
                    }
                }

                return this;
            }

            @NonNull
            public Extras.Builder addSharedElement(@NonNull View sharedElement, @NonNull String name) {
                this.mSharedElements.put(sharedElement, name);
                return this;
            }

            @NonNull
            public Extras build() {
                return new Extras(this.mSharedElements);
            }
        }
    }

    @ClassType(Fragment.class)
    public static class Destination extends NavDestination {
        private String mClassName;

        public Destination(@NonNull NavigatorProvider navigatorProvider) {
            this(navigatorProvider.getNavigator(HiFragmentNavigator.class));
        }

        public Destination(@NonNull Navigator<? extends Destination> fragmentNavigator) {
            super(fragmentNavigator);
        }

        @Override
        @CallSuper
        public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs, styleable.FragmentNavigator);
            String className = a.getString(styleable.FragmentNavigator_android_name);
            if (className != null) {
                this.setClassName(className);
            }

            a.recycle();
        }

        @NonNull
        public final Destination setClassName(@NonNull String className) {
            this.mClassName = className;
            return this;
        }

        @NonNull
        public final String getClassName() {
            if (this.mClassName == null) {
                throw new IllegalStateException("Fragment class was not set");
            } else {
                return this.mClassName;
            }
        }

        @Override
        @NonNull
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append(" class=");
            if (this.mClassName == null) {
                sb.append("null");
            } else {
                sb.append(this.mClassName);
            }

            return sb.toString();
        }
    }
}
