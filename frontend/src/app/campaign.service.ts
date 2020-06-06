import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Campaigns} from './apiobjects';

@Injectable({
  providedIn: 'root'
})
export class CampaignService {
  private campaignsUrl = 'https://www.loot.senorpez.com/campaigns';

  constructor(private http: HttpClient) { }

  getCampaigns(): Observable<Campaigns> {
    return this.http.get<Campaigns>(this.campaignsUrl);
  }
}
