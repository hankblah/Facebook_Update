package facebook_updater;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Scanner;

import javax.imageio.ImageIO;

import java.net.MalformedURLException;
import java.net.URL;

import javax.rmi.CORBA.Util;

import facebook4j.BatchRequest;
import facebook4j.Comment;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Media;
import facebook4j.PagableList;
import facebook4j.PagePhotoUpdate;
import facebook4j.Photo;
import facebook4j.PhotoUpdate;
import facebook4j.Post;
import facebook4j.PostUpdate;
import facebook4j.PrivacyBuilder;
import facebook4j.PrivacyParameter;
import facebook4j.PrivacyType;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.api.PhotoMethods;
import facebook4j.auth.AccessToken;

public class crawler {

	public static Books book = new Books();

	public static ArrayList<URL> booklist = new ArrayList<URL>();

	public static String Java_proj = "C:/Users/i077938/Documents/Java_proj/";
	public static String photopath = "C:/Users/i077938/Documents/Java_proj/photos/";
	public static String dropbox_path = "C:/Users/i077938/Documents/Java_proj/";
	public static String token = "";

	public static void main(String[] args) throws Exception {
		obtain_OAuth_token();
		read_pgid_from_file();
		get_data_from_list();
		save_imageURL_to_img_to_local(book.bookpic_URL, book.book_title);
		update_to_fb();
		write_to_csv();
	}

	/**
	 * This method prompt for facebook OAuth token
	 * precondition: A working OAuth token
	 * postcondition: captured OAuth token
	 * @param
	 * @return void
	 * @throws FacebookException
	 */
	private static void obtain_OAuth_token() throws FacebookException {
		System.out.println("Please enter a fresh and valid FB token: ");
		Scanner scan = new Scanner(System.in);
		token = scan.nextLine();
		scan.close();
	}

	/**
	 * This method extract pgid (eslite book ID) from selected books of URL and constructs a list of URL to be crawled from
	 * precondition: A file contains URL of a list of selected books
	 * postcondition: cons
	 * @param
	 * @return void
	 * @throws IOException
	 */
	private static void read_pgid_from_file() throws IOException {
		File pgid_list = new File(Java_proj + "booklist.txt");
		Scanner scan = new Scanner(pgid_list);

		while (scan.hasNextLine()) {
			String book_pgid = scan.nextLine();
			int start_pgid = book_pgid.indexOf("pgid");
			start_pgid = book_pgid.indexOf("=", start_pgid);
			String stripped_book_pgid = book_pgid.substring(start_pgid + 1, start_pgid + 17);
			String temp = "http://www.eslite.com/product.aspx?pgid=" + stripped_book_pgid;
			booklist.add(new URL(temp));
		}
		scan.close();
	}

	/**
	 * This method save image 
	 * precondition: A file contains URL of a list of selected books
	 * postcondition: cons
	 * @param URL of book picture , Title of the book
	 * @return void
	 * @throws Exception
	 */
	private static void save_imageURL_to_img_to_local(ArrayList<URL> bookpic_URL, ArrayList<String> book_title)
			throws Exception {

		for (int i = 0; i < bookpic_URL.size(); i++) {
			BufferedImage image = ImageIO.read(bookpic_URL.get(i));
			String book_filename = book_title.get(i).replaceAll(": ", "");
			System.out.println(book_filename + " file write: "
					+ ImageIO.write(image, "jpg", new File(photopath + book_filename + ".jpg")));
		}
	}

	/**
	 * This method does the heavy lifting to crawl respective information and save it into Books object.
	 * precondition: A collection of valid book URL
	 * postcondition: Detail information for each book are collected into Books object.
	 * @param
	 * @return void
	 * @throws IOException
	 */
	private static void get_data_from_list() throws IOException {

		boolean picture_cap = false;
		String img_url = null;
		String each_book_title;
		boolean has_drawer = false;
		int ready_to_read_description = 0;
		boolean has_translator = false;

		String cut1 = "";
		String book_description_temp = "";

		for (int i = 0; i < booklist.size(); i++) {

			URL url = booklist.get(i);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			System.out.println(booklist.get(i).toString());
			String inputLine;
			String des_inputLine;

			while ((inputLine = in.readLine()) != null) {
				// System.out.println(inputLine);
				if (inputLine.contains("ISBN 13")) {
					String result = inputLine.substring(9, 22);
					System.out.println("ISBN: " + result);
					book.book_isbn.add(result);
					continue;
				}

				if (inputLine.contains("ctl00_ContentPlaceHolder1_Product_detail_book1_dlSpec_ctl00_lblDescription")) {
					int start_of_pages = inputLine.indexOf('>');
					int end_of_pages = inputLine.indexOf('<', start_of_pages);
					int total_pages = Integer.valueOf(inputLine.substring(start_of_pages + 1, end_of_pages));
					book.pages.add(total_pages);
					System.out.println("頁數: " + total_pages + "\n\n");
					continue;
				}

				if (inputLine.contains("'/sub_cate.aspx?cate=")) {
					int start_of_majcate = inputLine.indexOf("'/sub_cate.aspx?cate=");
					start_of_majcate = inputLine.indexOf('>', start_of_majcate);
					int end_of_majcate = inputLine.indexOf('<', start_of_majcate);
					book.maj_cate.add(inputLine.substring(start_of_majcate + 1, end_of_majcate));
					int start_of_subcate = inputLine.indexOf("list", end_of_majcate);
					start_of_subcate = inputLine.indexOf('>', start_of_subcate);
					int end_of_subcate = inputLine.indexOf('<', start_of_subcate);
					book.sub_cate.add(inputLine.substring(start_of_subcate + 1, end_of_subcate));
					System.out.println("分類: " + inputLine.substring(start_of_majcate + 1, end_of_majcate) + "->"
							+ inputLine.substring(start_of_subcate + 1, end_of_subcate));
					continue;
				}
				if (inputLine.contains("ctl00_ContentPlaceHolder1_CharacterList_ctl01_CharacterName_ctl00_linkName")
						&& has_drawer == false) {
					int start_of_translator = inputLine.indexOf('>');
					int end_of_translator = inputLine.indexOf('<', start_of_translator);
					book.translator.add(inputLine.substring(start_of_translator + 1, end_of_translator));
					System.out.println("譯者: " + inputLine.substring(start_of_translator + 1, end_of_translator));
					has_translator = true;
					continue;
				}

				if (inputLine.contains("ctl00_ContentPlaceHolder1_CharacterList_ctl02_CharacterName_ctl00_linkName")
						&& has_drawer == true) {
					int start_of_translator = inputLine.indexOf('>');
					int end_of_translator = inputLine.indexOf('<', start_of_translator);
					book.translator.add(inputLine.substring(start_of_translator + 1, end_of_translator));
					System.out.println("譯者: " + inputLine.substring(start_of_translator + 1, end_of_translator));
					has_translator = true;
					continue;
				}

				if (inputLine.contains("出版日期 ／&nbsp")) {
					int start_of_release_date = inputLine.indexOf(';');
					int end_of_release_date = inputLine.indexOf('<', start_of_release_date);
					book.publish_date.add(inputLine.substring(start_of_release_date + 1, end_of_release_date));
					System.out.println("出版日期: " + inputLine.substring(start_of_release_date + 1, end_of_release_date));
					continue;
				}

				if (inputLine.contains("ctl00_ContentPlaceHolder1_lblProductName")) {
					// System.out.println(inputLine);
					int start_of_title = inputLine.indexOf(">") + 1;
					int end_of_title = inputLine.indexOf("<", start_of_title);
					each_book_title = inputLine.substring(start_of_title, end_of_title);
					book.book_title.add(each_book_title);
					System.out.println("書名: " + each_book_title);
					continue;
				}

				if (inputLine.contains("og:image")) {
					img_url = inputLine.substring(inputLine.indexOf("http"),
							inputLine.toLowerCase().lastIndexOf("jpg") + 3);
					book.bookpic_URL.add(new URL(img_url));
					System.out.println("Picture URL: " + img_url);
					continue;
				}

				if (inputLine.contains("ctl00_ContentPlaceHolder1_CharacterList_ctl00_CharacterName_ctl00_linkName")) {
					int start_of_writer_name = inputLine.lastIndexOf("\">") + 2;
					int end_of_writer_name = inputLine.indexOf("<", start_of_writer_name);
					book.writer_name.add(inputLine.substring(start_of_writer_name, end_of_writer_name));
					System.out.println("作者: " + inputLine.substring(start_of_writer_name, end_of_writer_name));
					continue;
				}

				if (inputLine.contains("繪者/攝影者")) {
					has_drawer = true;
					continue;
				}

				if (inputLine.contains("出版社 ／")) {
					// System.out.println(inputLine);
					int start_of_publisher = inputLine.indexOf(">") + 1;
					int end_of_publisher = inputLine.indexOf("<", start_of_publisher);
					book.publisher.add(inputLine.substring(start_of_publisher, end_of_publisher));
					System.out.println("出版社: " + inputLine.substring(start_of_publisher, end_of_publisher));
					continue;
				}

				if (inputLine.contains("定價 ")) {
					int start_of_price = inputLine.indexOf(">") + 1;
					int end_of_price = inputLine.indexOf("<", start_of_price);
					int original_price = Integer.valueOf(inputLine.substring(start_of_price, end_of_price));
					System.out.println("定價: $" + original_price + " NTD");
					book.ori_price.add(original_price);
					continue;
				}

				if (inputLine.contains("售價 ／")) {
					// System.out.println(inputLine);
					int start_of_discount = inputLine.indexOf(">") + 1;
					int end_of_discount = inputLine.indexOf("<", start_of_discount);
					int booksales_discount = Integer.valueOf(inputLine.substring(start_of_discount, end_of_discount));

					// post processing to handle 9折, 8折, 7折 etc... (to avoid
					// /100 becoming 0.09, 0.08, 0.07...etc)
					if (booksales_discount < 10) {
						booksales_discount = booksales_discount * 10;
					}

					if (booksales_discount > 100) {
						booksales_discount = 0;
					}
					book.discount.add((double) booksales_discount / 100);
					System.out.println("折扣: " + (float) booksales_discount / 100);

					int start_of_salesprice = inputLine.indexOf("'>") + 2;
					int end_of_salesprice = inputLine.indexOf("<", start_of_salesprice);
					int book_salesprice = Integer.valueOf(inputLine.substring(start_of_salesprice, end_of_salesprice));
					System.out.println("售價: $" + book_salesprice + " NTD");
					book.sales_price.add((book_salesprice));
					continue;
				}

				if (inputLine.contains("<h2>內容簡介") || ready_to_read_description == 1) {
					ready_to_read_description++;
					if (ready_to_read_description == 1) {
						System.out.print("內容簡介:");
					}
					continue;
				}

				if (ready_to_read_description > 0 && !inputLine.contains("<a href=\"#top\"")) {
					// System.out.println("@@@@@@ " + inputLine);
					cut1 = inputLine.replaceAll("<br>", " ").replaceAll("<b>", "").replaceAll("</b>", "")
							.replaceAll("&nbsp;", "").replaceAll("&amp", "").replaceAll("<br/>", "")
							.replaceAll("</div>", "").replaceAll("<div>", "").replaceAll("</font>", "")
							.replaceAll("<div align=\"center\">", "")
							.replaceAll("<div style=\"text-align: center;\">", "")
							.replaceAll("<font color=\".......\">", "")
							.replaceAll("<div style=\"text-align: left;\">", "");
					book_description_temp = book_description_temp.concat(cut1.trim());
					cut1 = "";
					System.out.print(book_description_temp);
					continue;
				}
				if (inputLine.contains("<a href=\"#top\"") && ready_to_read_description > 0) {
					book.book_desc.add(book_description_temp);
					// System.out.println("@@@i should only print once");
					ready_to_read_description = 0;
					continue;
				}
			}

			// end of the crawler operation
			if (has_translator == false) {
				book.translator.add(" ");
			}
			book_description_temp = "";
			has_translator = false;
			has_drawer = false;
			picture_cap = false;
		}
	}

	/**
	 * 
	 * This method updates the books to the customer bookstore (fanpage)
	 * 
	 * precondition: A working OAuth token, cover page pictures, 
	 * postcondition: Facebook posts of selected book on fanpage.
	 * @param
	 * @return void
	 * @throws IOException
	 * @throws FacebookException
	 * @throws InterruptedException
	 */
	private static void update_to_fb() throws IOException, FacebookException, InterruptedException {

		// Generate facebook instance.
		Facebook facebook = new FacebookFactory().getInstance();
		// Use default values for oauth app id.
		facebook.setOAuthAppId("", "");

		// Get an access token from:
		// https://developers.facebook.com/tools/explorer
		// Copy and paste it below.
		AccessToken at = new AccessToken(token);
		// Set access token.
		facebook.setOAuthAccessToken(at);

		// We're done.
		// Access group feeds.
		// You can get the group ID from:
		// https://developers.facebook.com/tools/explorer

		for (int i = 0; i < booklist.size(); i++) {
			Media media = new Media(new File(photopath + book.book_title.get(i).replaceAll(": ", "") + ".jpg"));
			book.media_update.add(media);
		}

		for (int j = 0; j < book.media_update.size(); j++) {
			try {
				// hankblah-test bookstore ID
				// facebook.addAlbumPhoto("1795876700639502",
				// media_update.get(j), "書名:\n" + book_title.get(j)+ "\n\n" +
				// "內容簡介:\n" + book_desc.get(j));
				
				// Fantasma bookstore ID
				facebook.addAlbumPhoto("1631583733733229", book.media_update.get(j),
						"書名:\n" + book.book_title.get(j) + "\n\n" + "內容簡介:\n" + book.book_desc.get(j));

				System.out.println(book.book_title.get(j) + "\t\tis posted on facebook !!!!!");

				//handling if failed to post update
			} catch (FacebookException e) {
				System.out.println(book.book_title.get(j) + " cannot be posted on Facebook! Proceed to removed from booklist - "
						+ e.getErrorMessage());
				for (int k = 0; k < booklist.size(); k++) {

					book.remove_failed_book(j);
				}
				j--;
			} finally {
				//creates wait time to allow facebook to process
				Thread.sleep(5000);
			}
		}
		book.bookpic_URL.clear();

		// blah-test bookstore ID
		// ResponseList<Photo> feeds =
		// facebook.getUploadedPhotos("1795876700639502", new
		// Reading().limit(booklist.size()));

		
		// Below function obtains the absolute URL on facebook to be process further and store to local MySQL database		
		
		// Fantasma bookstore ID
		ResponseList<Photo> feeds = facebook.getUploadedPhotos("1631583733733229",
				new Reading().limit(booklist.size()));
		// System.out.println("@@@@@@@@@ booklist size is : " + booklist.size()
		// + "\t\t feeds size is : " + feeds.size() + "media_size is : " +
		// media_update.size());
		for (int i = feeds.size() - 1; i >= 0; i--) {
			Photo photo = feeds.get(i);
			URL url = photo.getSource();
			// System.out.println(url);
			book.bookpic_URL.add(url);
		}
	}

	/**
	 * This method constructs the TAB delimiter txt file to be later read by excel and MySQL
	 * @param
	 * @return void
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	private static void write_to_csv() throws Exception, FileNotFoundException {
		// start to process and save image to local .csv file
		Writer out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(Java_proj + "text_to_be_read_by_excel.txt"), "UTF-8"));
		String book_entries_on_excel = "";
		book_entries_on_excel = "Item No.";
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("書名");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("作者");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("譯者(如沒有請留空)");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("出版社");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("出版日期(月/日/年)");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("ISBN");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("定價 NT$");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("折扣");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("售價 NT$");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("內容簡介");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("頁數");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("主分類");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("次分類");
		book_entries_on_excel = book_entries_on_excel.concat("\t");
		book_entries_on_excel = book_entries_on_excel.concat("URL");
		book_entries_on_excel = book_entries_on_excel.concat("\n");
		out.write(book_entries_on_excel);

		for (int i = 0; i < booklist.size(); i++) {
			book_entries_on_excel = "";
			book_entries_on_excel = book_entries_on_excel.concat(String.valueOf(i + 1));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(book.book_title.get(i));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(book.writer_name.get(i));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(book.translator.get(i));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(book.publisher.get(i));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(book.publish_date.get(i));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(book.book_isbn.get(i));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(String.valueOf(book.ori_price.get(i)));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(String.valueOf(book.discount.get(i)));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(String.valueOf(book.sales_price.get(i)));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(book.book_desc.get(i));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(String.valueOf(book.pages.get(i)));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(book.maj_cate.get(i));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(book.sub_cate.get(i));
			book_entries_on_excel = book_entries_on_excel.concat("\t");
			book_entries_on_excel = book_entries_on_excel.concat(book.bookpic_URL.get(i).toExternalForm());
			book_entries_on_excel = book_entries_on_excel.concat("\n");
			out.write(book_entries_on_excel);
		}
		out.close();
	}
}
