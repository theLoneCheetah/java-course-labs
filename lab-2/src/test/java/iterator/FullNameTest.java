package iterator;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class FullNameTest {

    // Проверка возврата корректного ФИО
    @Test
    void iteratesFullNameInOrder() {
        FullName fullName = new FullName("Ivanov", "Ivan", "Ivanovich");
        MyIterator<String> it = fullName.iterator();

        assertTrue(it.hasNext());
        assertEquals("Ivanov", it.next());
        assertTrue(it.hasNext());
        assertEquals("Ivan", it.next());
        assertTrue(it.hasNext());
        assertEquals("Ivanovich", it.next());
        assertFalse(it.hasNext());
    }

    // Проверка пустого ФИО
    @Test
    void emptyFullNameHasNoElements() {
        FullName fullName = new FullName();
        MyIterator<String> it = fullName.iterator();

        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    // Только фамилия
    @Test
    void singlePartIsIteratedOnce() {
        FullName fullName = new FullName("Ivanov");
        MyIterator<String> it = fullName.iterator();

        assertTrue(it.hasNext());
        assertEquals("Ivanov", it.next());
        assertFalse(it.hasNext());
    }

    // Проверка ошибки после повторного итерирования
    @Test
    void nextAfterEndThrows() {
        FullName fullName = new FullName("Ivanov");
        MyIterator<String> it = fullName.iterator();

        it.next();
        assertThrows(NoSuchElementException.class, it::next);
        assertThrows(NoSuchElementException.class, it::next);
    }

    // Независимость двух итераторов
    @Test
    void twoIteratorsAreIndependent() {
        FullName fullName = new FullName("Ivanov", "Ivan", "Ivanovich");
        MyIterator<String> a = fullName.iterator();
        MyIterator<String> b = fullName.iterator();

        assertEquals("Ivanov", a.next());
        assertEquals("Ivanov", b.next());
        assertEquals("Ivan", a.next());
        assertEquals("Ivan", b.next());
    }

    // Исключение при присваивании null
    @Test
    void nullArrayIsRejected() {
        assertThrows(NullPointerException.class, () -> new FullName((String[]) null));
    }

    // Проверка сохранения целостности при изменении внешнего массива
    @Test
    void externalArrayMutationDoesNotAffect() {
        String[] source = {"Ivanov", "Ivan"};
        FullName fullName = new FullName(source);
        source[0] = "Mutated";

        MyIterator<String> it = fullName.iterator();
        assertEquals("Ivanov", it.next());
        assertEquals("Ivan", it.next());
    }
}