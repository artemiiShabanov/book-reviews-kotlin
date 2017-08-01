package services

/**
 * Simple string comparing
 */
fun equalStrings(s1: String?, s2: String?): Boolean = (s1?.trim() == s2?.trim())

/**
 * Author string comparing
 */
fun equalAuthors(s1: String?, s2: String?): Boolean {

        var list1: List<String>? = s1?.trim()?.split(" ")
        var list2: List<String>? = s2?.trim()?.split(" ")

        return (list1?.size == list2?.size) && list1?.containsAll(list2 as Collection<String>)?: false
}

/**
 * Hash for simple string.
 */
fun smartHash(s: String): Int {
    val P = 31
    var hash = 0
    var p_pow = 1
    for (c in s) {
        hash += (c - 'a' + 1) * p_pow
        p_pow *= P
    }
    return hash
}

/**
 * Hash for string-author(first & second name).
 */
fun stupidHash(s: String): Int {
    var hash = 0
    for (c in s) {
        hash += c - 'a' + 1
    }
    return hash
}