package facebook_updater;

import java.net.URL;
import java.util.ArrayList;

import facebook4j.Media;

public class Books {

	public ArrayList<String> book_title = new ArrayList<String>();
	public ArrayList<String> book_isbn = new ArrayList<String>();
	public ArrayList<Integer> ori_price = new ArrayList<Integer>();
	public ArrayList<URL> bookpic_URL = new ArrayList<URL>();
	public ArrayList<String> writer_name = new ArrayList<String>();
	public ArrayList<String> translator = new ArrayList<String>();
	public ArrayList<String> publisher = new ArrayList<String>();
	public ArrayList<String> publish_date = new ArrayList<String>();
	public ArrayList<Integer> sales_price = new ArrayList<Integer>();
	public ArrayList<Double> discount = new ArrayList<Double>();
	public ArrayList<Integer> pages = new ArrayList<Integer>();
	public ArrayList<String> book_desc = new ArrayList<String>();
	public ArrayList<String> maj_cate = new ArrayList<String>();
	public ArrayList<String> sub_cate = new ArrayList<String>();
	public ArrayList<Media> media_update = new ArrayList<Media>();

	public boolean remove_failed_book(int index) {
		try {
			book_title.remove(index);
			book_isbn.remove(index);
			ori_price.remove(index);
			bookpic_URL.remove(index);
			writer_name.remove(index);
			translator.remove(index);
			publisher.remove(index);
			publish_date.remove(index);
			sales_price.remove(index);
			discount.remove(index);
			pages.remove(index);
			book_desc.remove(index);
			maj_cate.remove(index);
			sub_cate.remove(index);
			media_update.remove(index);
			
			System.out.println("Removed " + index + "th book in the list !");
			return true;

		} catch (IndexOutOfBoundsException e) {
			System.out.println("Failed to remove " + index + "th book in the list !");
			return false;
		}
	}
}
