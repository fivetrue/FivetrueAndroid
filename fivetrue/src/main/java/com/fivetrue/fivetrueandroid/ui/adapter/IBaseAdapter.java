package com.fivetrue.fivetrueandroid.ui.adapter;

import java.util.List;

/**
 * Created by ojin.kwon on 2016-05-19.
 */
public interface IBaseAdapter <T> {

    /**
     * 특정 위치의 선택 여부를 확인 한다
     * @param position
     * @return
     */
    boolean isSelected(int position);

    /**
     * 특정 위치의 선택 여부를 toggle할 때 사용된다
     * @param position
     */
    void toggleSelected(int position);

    /**
     * 지정된 position 의 선택 여부를 SparseBooleanArray에 저장한다
     * @param position
     * @param isSelected
     */
    void selectItem(int position, boolean isSelected);

    /**
     * SparseBooleanArray의 값들을 초기화한다
     */
    void clearSelections();

    /**
     *
     * @param pos
     * @return
     */
    T getItem(int pos);

    /**
     *
     * @param datas
     */
    void setData(List<T> datas);

    int getCount();
}
