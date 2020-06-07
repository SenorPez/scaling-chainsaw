import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Campaign, Campaigns, Characters, EmbeddedCampaign, Index} from './apiobjects';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'https://www.loot.senorpez.com/';

  constructor(private http: HttpClient) { }

  getIndex(): Observable<Index> {
    return this.http.get<Index>(this.apiUrl);
  }

  getCampaigns(index: Index): Observable<Campaigns> {
    return this.http.get<Campaigns>(index._links['loot-api:campaigns'].href);
  }

  getCampaign(embeddedCampaign: EmbeddedCampaign): Observable<Campaign> {
    return this.http.get<Campaign>(embeddedCampaign._links.self.href);
  }

  getCharacters(campaign: Campaign): Observable<Characters> {
    return this.http.get<Characters>(campaign._links['loot-api:characters'].href);
  }
  //
  // getCampaign(campaign: Campaign): Observable<Campaign> {
  //   return this.http.get<Campaign>(campaign._links.)
  // }
  //
  // getCharacters(campaign: Campaign): Observable<Characters> {
  //   return this.http.get<Characters>(campaign._links['loot-api:characters'].href);
  // }
}
