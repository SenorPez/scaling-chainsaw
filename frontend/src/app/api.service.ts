import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Campaigns, Index} from './apiobjects';

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
}
