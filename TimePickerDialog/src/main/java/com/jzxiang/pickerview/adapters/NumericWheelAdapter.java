/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.jzxiang.pickerview.adapters;

import android.content.Context;

import java.util.ArrayList;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter extends AbstractWheelTextAdapter {

    private ArrayList<String> datas;
    /**
     * Constructor
     *
     * @param context  the current context
     * @param datas the wheel datas
     */
    public NumericWheelAdapter(Context context,  ArrayList<String> datas) {
        super(context);
        this.datas = datas;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {
            return datas.get(index);
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return datas.size();
    }


}
