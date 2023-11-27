package com.yrezgui.filepicker.pickercompose.picker

operator fun <T> LinkedHashSet<T>.plus(newItem: T): LinkedHashSet<T> {
    val newLinkedHashSet = linkedSetOf<T>()

    val itr = this.iterator()

    while (itr.hasNext()) {
        newLinkedHashSet.add(itr.next())
    }

    newLinkedHashSet.add(newItem)

    return newLinkedHashSet
}

operator fun <T> LinkedHashSet<T>.minus(newItem: T): LinkedHashSet<T> {
    val newLinkedHashSet = linkedSetOf<T>()

    val itr = this.iterator()

    while (itr.hasNext()) {
        newLinkedHashSet.add(itr.next())
    }

    newLinkedHashSet.remove(newItem)

    return newLinkedHashSet
}