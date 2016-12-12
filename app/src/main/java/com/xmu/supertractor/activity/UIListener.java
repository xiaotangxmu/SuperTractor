package com.xmu.supertractor.activity;



public interface UIListener {
    void flush_biggest_out_player();

    void show_push_off();

    void ready_next_turn();

    void clear_cards();

    void flush_score();

    void show_eight_cards();

    void push_on();

    void push_off();

    void broadcast_push();

    void start_round();

    void flush_lord_color_img();

    void others_gai_di_pai();

    void gai_di_pai();

    void flush_status();

    void start_call();

    void call_info(int a,int b,int c);

    void flush_my_card();
}
