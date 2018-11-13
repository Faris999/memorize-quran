package org.faris.memorizequran;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao()
public interface VerseDao {

    @Query("SELECT * FROM verse")
    List<Verse> getAll();

    @Query("SELECT * FROM verse WHERE surah = :surah")
    List<Verse> getBySurah(int surah);

    @Insert
    void insert(Verse verse);

    @Query("UPDATE verse SET memorized = :memorized WHERE surah = :surah AND num = :num")
    void update(int surah, int num, boolean memorized);

}
