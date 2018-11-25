/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.madanes.myapp.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.transaction

fun FragmentActivity.replaceFragment(@IdRes containerViewId: Int, fragment: Fragment)
{
    supportFragmentManager.transaction(now = false, allowStateLoss = false) {
        replace(containerViewId, fragment, fragment::class.java.simpleName)
    }

}

fun FragmentActivity.addFragment(@IdRes containerViewId: Int, fragment: Fragment)
{
    supportFragmentManager.beginTransaction()
            .add(containerViewId, fragment, fragment::class.java.simpleName)
            .addToBackStack(fragment::class.java.simpleName).commit()
}

fun FragmentActivity.findFragmentByTag(tag: String): Fragment? =
        supportFragmentManager.findFragmentByTag(tag)

fun FragmentActivity.findFragmentById(tag: Int): Fragment? =
        supportFragmentManager.findFragmentById(tag)
