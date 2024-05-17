import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class ArticleDetailBean(
    val author: String,
    val fresh: Boolean,
    val articleId: Int,
    val link: String,
    var niceDate: String,
    val shareUser: String,
    val title: String,
    val superChapterId: Int,
    val superChapterName: String,
    var collect: Boolean
)

data class SearchHistoryBean(
    val username: String,
    val searchString: String
)

data class CollectBean(
    val username: String,
    val articleId: Int
)

class DBHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    //文章idset
//    private val artIdSet = mutableSetOf<Int>()

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "articles"
        private const val TABLE_SEARCH_NAME = "searchHistory"
        private const val TABLE_COLLECT_NAME = "collect"
        // 列名
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_SEARCH_STRING = "searchString"
        private const val COLUMN_ID = "id"
        private const val COLUMN_AUTHOR = "author"
        private const val COLUMN_FRESH = "fresh"
        private const val COLUMN_ARTICLE_ID = "articleId"
        private const val COLUMN_LINK = "link"
        private const val COLUMN_NICE_DATE = "niceDate"
        private const val COLUMN_SHARE_USER = "shareUser"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_SUPER_CHAPTER_ID = "superChapterId"
        private const val COLUMN_SUPER_CHAPTER_NAME = "superChapterName"
        private const val COLUMN_COLLECT = "collect"

        @Volatile
        private var instance: DBHelper? = null

        fun getInstance(context: Context): DBHelper {
            return instance ?: synchronized(this) {
                instance ?: DBHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_AUTHOR TEXT," +
                "$COLUMN_FRESH INTEGER," +
                "$COLUMN_ARTICLE_ID INTEGER UNIQUE," +
                "$COLUMN_LINK TEXT," +
                "$COLUMN_NICE_DATE TEXT," +
                "$COLUMN_SHARE_USER TEXT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_SUPER_CHAPTER_ID INTEGER," +
                "$COLUMN_SUPER_CHAPTER_NAME TEXT," +
                "$COLUMN_COLLECT INTEGER" +
                ")"

        val createSearchHistoryTableQuery = "CREATE TABLE $TABLE_SEARCH_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_SEARCH_STRING TEXT," +
                "UNIQUE ($COLUMN_USERNAME, $COLUMN_SEARCH_STRING)" +
                ")"

        val createCollectTableQuery = "CREATE TABLE $TABLE_COLLECT_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_ARTICLE_ID INTEGER," +
                "UNIQUE ($COLUMN_USERNAME, $COLUMN_ARTICLE_ID)" +
                ")"

        db.execSQL(createTableQuery)
        db.execSQL(createSearchHistoryTableQuery)
        db.execSQL(createCollectTableQuery)


    }

    private fun isArticleExist(articleId: Int): Boolean {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID),
            "$COLUMN_ARTICLE_ID = ?",
            arrayOf(articleId.toString()),
            null,
            null,
            null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    private fun isSearchHistoryExist(username: String, searchString: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_SEARCH_STRING = ?"
        val selectionArgs = arrayOf(username, searchString)
        val cursor: Cursor = db.query(
            TABLE_SEARCH_NAME,  // 使用正确的表名常量
            arrayOf(COLUMN_SEARCH_STRING), // 此处只需要选择一个列，因为我们只关心记录是否存在
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    private fun isCollectExist(username: String, articleId: Int): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_ARTICLE_ID = ?"
        val selectionArgs = arrayOf(username, articleId.toString())
        val cursor: Cursor = db.query(
            TABLE_COLLECT_NAME,
            arrayOf(COLUMN_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertArticle(article: ArticleDetailBean) {
        if (isArticleExist(article.articleId)) {
            return
        }

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AUTHOR, article.author)
            put(COLUMN_FRESH, if (article.fresh) 1 else 0)
            put(COLUMN_ARTICLE_ID, article.articleId)
            put(COLUMN_LINK, article.link)
            put(COLUMN_NICE_DATE, article.niceDate)
            put(COLUMN_SHARE_USER, article.shareUser)
            put(COLUMN_TITLE, article.title)
            put(COLUMN_SUPER_CHAPTER_ID, article.superChapterId)
            put(COLUMN_SUPER_CHAPTER_NAME, article.superChapterName)
            put(COLUMN_COLLECT, if (article.collect) 1 else 0)
        }
        db.insert(TABLE_NAME, null, values)
    }

    fun insertSearchHistory(historyBean: SearchHistoryBean) {
        if (isSearchHistoryExist(historyBean.username, historyBean.searchString)) {
            return
        }

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, historyBean.username)
            put(COLUMN_SEARCH_STRING, historyBean.searchString)
        }
        db.insert(TABLE_SEARCH_NAME, null, values)
    }

    fun insertCollect(collectBean: CollectBean) {
        if (isCollectExist(collectBean.username, collectBean.articleId)) {
            return
        }

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, collectBean.username)
            put(COLUMN_ARTICLE_ID, collectBean.articleId)
        }
        db.insert(TABLE_COLLECT_NAME, null, values)
    }

    fun getAllArticles(): List<ArticleDetailBean> {
        val articlesList = mutableListOf<ArticleDetailBean>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            val authorIndex = cursor.getColumnIndex(COLUMN_AUTHOR)
            val freshIndex = cursor.getColumnIndex(COLUMN_FRESH)
            val articleIdIndex = cursor.getColumnIndex(COLUMN_ARTICLE_ID)
            val linkIndex = cursor.getColumnIndex(COLUMN_LINK)
            val niceDateIndex = cursor.getColumnIndex(COLUMN_NICE_DATE)
            val shareUserIndex = cursor.getColumnIndex(COLUMN_SHARE_USER)
            val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
            val superChapterIdIndex = cursor.getColumnIndex(COLUMN_SUPER_CHAPTER_ID)
            val superChapterNameIndex = cursor.getColumnIndex(COLUMN_SUPER_CHAPTER_NAME)
            val collectIndex = cursor.getColumnIndex(COLUMN_COLLECT)

            while (!cursor.isAfterLast) {
                if (authorIndex != -1 && freshIndex != -1 && articleIdIndex != -1 &&
                    linkIndex != -1 && niceDateIndex != -1 && shareUserIndex != -1 &&
                    titleIndex != -1 && superChapterIdIndex != -1 && superChapterNameIndex != -1 &&
                    collectIndex != -1
                ) {
                    val author = cursor.getString(authorIndex)
                    val fresh = cursor.getInt(freshIndex) == 1
                    val articleId = cursor.getInt(articleIdIndex)
                    val link = cursor.getString(linkIndex)
                    val niceDate = cursor.getString(niceDateIndex)
                    val shareUser = cursor.getString(shareUserIndex)
                    val title = cursor.getString(titleIndex)
                    val superChapterId = cursor.getInt(superChapterIdIndex)
                    val superChapterName = cursor.getString(superChapterNameIndex)
                    val collect = cursor.getInt(collectIndex) == 1

                    val article = ArticleDetailBean(
                        author, fresh, articleId, link, niceDate, shareUser, title,
                        superChapterId, superChapterName, collect
                    )
                    articlesList.add(article)
                }
                cursor.moveToNext()
            }
        }
        cursor.close()
//        db.close()
        return articlesList
    }

    // DBHelper类中新增的方法
    fun searchArticlesByTitle(searchTitle: String): List<ArticleDetailBean> {
        return getAllArticles().filter { it.title.contains(searchTitle) }
    }

    fun getSearchHistoryForUser(username: String): List<SearchHistoryBean> {
        val db = readableDatabase
        val searchHistoryList = mutableListOf<SearchHistoryBean>()
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)

        val cursor: Cursor = db.query(
            TABLE_SEARCH_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val searchString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEARCH_STRING))
                searchHistoryList.add(SearchHistoryBean(username, searchString))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return searchHistoryList
    }

    fun deleteSearchHistoryBySearchString(searchString: String): Int {
        val db = readableDatabase
        val whereClause = "$COLUMN_SEARCH_STRING = ?"
        val whereArgs = arrayOf(searchString)
        val deletedRows = db.delete(TABLE_SEARCH_NAME, whereClause, whereArgs)
        db.close()
        return deletedRows
    }

    public fun getCollectForUser(username: String): List<CollectBean> {
        val db = readableDatabase
        val CollectBeanList = mutableListOf<CollectBean>()
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)

        val cursor: Cursor = db.query(
            TABLE_COLLECT_NAME,
            null, // 选择所有列
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val articleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ARTICLE_ID))
                CollectBeanList.add(CollectBean(username, articleId))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return CollectBeanList
    }
}
