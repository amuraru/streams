package org.jwall.sql.audit;

import org.junit.Test;

import stream.data.TreeNode;

public class ParseSQLTest
{

    
    @Test
    public void test(){
    }

    
    /**
     * @param args
     */
    public static void main(String[] args)
        throws Exception
    {
        String sql = "INSERT INTO `wp_options` (`option_name`, `option_value`, `autoload`) VALUES ('wp_user_roles', 'a:5:{s:13:\"administrator\";a:2:{s:4:\"name\";s:13:\"Administrator\";s:12:\"capabilities\";a:24:{s:13:\"switch_themes\";b:1;s:11:\"edit_themes\";b:1;s:16:\"activate_plugins\";b:1;s:12:\"edit_plugins\";b:1;s:10:\"edit_users\";b:1;s:10:\"edit_files\";b:1;s:14:\"manage_options\";b:1;s:17:\"moderate_comments\";b:1;s:17:\"manage_categories\";b:1;s:12:\"manage_links\";b:1;s:12:\"upload_files\";b:1;s:6:\"import\";b:1;s:15:\"unfiltered_html\";b:1;s:10:\"edit_posts\";b:1;s:17:\"edit_others_posts\";b:1;s:20:\"edit_published_posts\";b:1;s:13:\"publish_posts\";b:1;s:10:\"edit_pages\";b:1;s:4:\"read\";b:1;s:8:\"level_10\";b:1;s:7:\"level_9\";b:1;s:7:\"level_8\";b:1;s:7:\"level_7\";b:1;s:7:\"level_6\";b:1;}}s:6:\"editor\";a:2:{s:4:\"name\";s:6:\"Editor\";s:12:\"capabilities\";a:0:{}}s:6:\"author\";a:2:{s:4:\"name\";s:6:\"Author\";s:12:\"capabilities\";a:0:{}}s:11:\"contributor\";a:2:{s:4:\"name\";s:11:\"Contributor\";s:12:\"capabilities\";a:0:{}}s:10:\"subscriber\";a:2:{s:4:\"name\";s:10:\"Subscriber\";s:12:\"capabilities\";a:0:{}}}', 'yes')";
        String sql2 = "INSERT INTO `wp_options` (`option_name`, `option_value`, `autoload`) VALUES ('wp_user_roles', 'a:5:{s:13:\"administrator\";a:2:{s:4:\"name\";s:13:\"Administrator\";s:12:\"capabilities\";a:24:{s:13:\"switch_themes\";b:1;s:11:\"edit_themes\";b:1;s:16:\"activate_plugins\";b:1;s:12:\"edit_plugins\";b:1;s:10:\"edit_users\";b:1;s:10:\"edit_files\";b:1;s:14:\"manage_options\";b:1;s:17:\"moderate_comments\";b:1;s:17:\"manage_categories\";b:1;s:12:\"manage_links\";b:1;s:12:\"upload_files\";b:1;s:6:\"import\";b:1;s:15:\"unfiltered_html\";b:1;s:10:\"edit_posts\";b:1;s:17:\"edit_others_posts\";b:1;s:20:\"edit_published_posts\";b:1;s:13:\"publish_posts\";b:1;s:10:\"edit_pages\";b:1;s:4:\"read\";b:1;s:8:\"level_10\";b:1;s:7:\"level_9\";b:1;s:7:\"level_8\";b:1;s:7:\"level_7\";b:1;s:7:\"level_6\";b:1;}}s:6:\"editor\";a:2:{s:4:\"name\";s:6:\"Editor\";s:12:\"capabilities\";a:0:{}}s:6:\"author\";a:2:{s:4:\"name\";s:6:\"Author\";s:12:\"capabilities\";a:0:{}}s:11:\"contributor\";a:2:{s:4:\"name\";s:11:\"Contributor\";s:12:\"capabilities\";a:0:{}}s:10:\"subscriber\";a:2:{s:4:\"name\";s:10:\"Subscriber\";s:12:\"capabilities\";a:0:{}}}', 'yes') ON DUPLICATE KEY UPDATE `option_name` = VALUES(`option_name`), `option_value` = VALUES(`option_value`), `autoload` = VALUES(`autoload`)";
        
        SQLStreamParser parser = new SQLStreamParser();
        //parser.getFixes().put( "SQL_CALC_FOUND_ROWS", "" );
        parser.getFixes().put( " AS CHAR)", ")");
        TreeNode tree = parser.parse( sql );
        System.out.println( "tree-1 is: " + tree );

        sql2 = "SELECT CONCAT(CHAR(58,117,118,115,58),CHAR(88,97,102,104,115,69,121,103,86,117),CHAR(32),CHAR(58,101,103,98,58)) FROM users";
        sql2 = "SELECT NULL,CONCAT(CHAR(58,117,118,115,58),IFNULL(CAST(CHAR(88,97,102,104,115,69,121,103,86,117) AS CHAR),CHAR(32)),CHAR(58,101,103,98,58)) FROM USERS";
        sql2 = "SELECT NULL,CONCAT(CHAR(58,117,118,115,58),IFNULL(CAST(CHAR(88,97,102,104,115,69,121,103,86,117)),CHAR(32)),CHAR(58,101,103,98,58)),NULL FROM _NULL_0 UNION SELECT NULL,CONCAT(CHAR(58,117,118,115,58),IFNULL(CAST(CHAR(75,90,74,117,100,98,68,78,105,98)),CHAR(32)),CHAR(58,101,103,98,58)),NULL FROM _NULL_";
        //sql2 = "SELECT NULL,CONCAT(CHAR(58,117,118,115,58),IFNULL(CAST(CHAR(88,97,102,104,115,69,121,103,86,117)))) FROM EVENTS  UNION  SELECT DATE FROM USERS  UNION SELECT ID FROM FRIENDS";
        //sql2 = "SELECT CONCAT('asdf','asf',CHAR(123,234)),CHAR(23) FROM USERS";
        sql2 = "SELECT id,name,price FROM products WHERE id = -1259 UNION ALL SELECT NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL";
        sql2 = "SELECT id,name,price FROM products WHERE id = 1 UNION ALL SELECT NULL, CONCAT(CHAR(58,117,118,115,58),IFNULL(CAST(schema_name AS CHAR),CHAR(32)),CHAR(58,101,103,98,58)), NULL FROM information_schema.SCHEMATA";
        tree = parser.parse( sql2 );
        System.out.println( "tree-2 is: " + tree );

        sql2 = "SELECT COUNT(),1+2";
        tree = parser.parse( sql2 );
        System.out.println( "tree-3 is: " + tree );

    }
}